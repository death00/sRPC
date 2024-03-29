package com.death00.handler;

import com.death00.common.MessageInput;
import com.death00.common.MessageRegistry;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author death00
 * @date 2019/9/16
 */
@Sharable
public class ServerMessageCollector extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerMessageCollector.class);

    private ThreadPoolExecutor executor;
    private MessageHandlers handlers;
    private MessageRegistry registry;

    public ServerMessageCollector(MessageHandlers handlers, MessageRegistry registry, int workerThreads) {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1000);
        ThreadFactory factory = new ThreadFactory() {

            AtomicInteger seq = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("rpc-" + seq.getAndIncrement());
                return t;
            }

        };
        this.executor = new ThreadPoolExecutor(1, workerThreads, 30, TimeUnit.SECONDS, queue,
                factory,
                new CallerRunsPolicy());
        this.handlers = handlers;
        this.registry = registry;
    }

    public void closeGracefully() {
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        this.executor.shutdownNow();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("connection comes");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("connection leaves");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MessageInput) {
            this.executor.execute(() -> {
                this.handleMessage(ctx, (MessageInput) msg);
            });
        }
    }

    private void handleMessage(ChannelHandlerContext ctx, MessageInput input) {
        // 业务逻辑在这里
        Class<?> clazz = registry.get(input.getType());
        if (clazz == null) {
            handlers.defaultHandler().handle(ctx, input.getRequestId(), input);
            return;
        }
        Object o = input.getPayload(clazz);
        @SuppressWarnings("unchecked")
        IMessageHandler<Object> handler = (IMessageHandler<Object>) handlers.get(input.getType());
        if (handler != null) {
            handler.handle(ctx, input.getRequestId(), o);
        } else {
            handlers.defaultHandler().handle(ctx, input.getRequestId(), input);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn("connection error", cause);
    }

}
