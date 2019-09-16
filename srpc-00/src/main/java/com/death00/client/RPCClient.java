package com.death00.client;

import com.death00.common.MessageDecoder;
import com.death00.common.MessageEncoder;
import com.death00.common.MessageOutput;
import com.death00.common.MessageRegistry;
import com.death00.exception.RPCException;
import com.death00.handler.ClientMessageCollector;
import com.death00.util.RequestId;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author death00
 * @date 2019/9/16
 */
public class RPCClient {

    private final static Logger LOG = LoggerFactory.getLogger(RPCClient.class);

    private String ip;
    private int port;
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private ClientMessageCollector collector;
    private boolean started;
    private boolean stopped;

    private MessageRegistry registry = new MessageRegistry();

    public RPCClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.init();
    }

    public RPCClient rpc(String type, Class<?> reqClass) {
        registry.register(type, reqClass);
        return this;
    }

    public <T> RpcFuture<T> sendAsync(String type, Object payload) {
        if (!started) {
            connect();
            started = true;
        }
        String requestId = RequestId.next();
        MessageOutput output = MessageOutput.builder()
                .requestId(requestId)
                .type(type)
                .response(payload)
                .build();
        return collector.send(output);
    }

    public <T> T send(String type, Object payload) {
        RpcFuture<T> future = sendAsync(type, payload);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RPCException(e);
        }
    }

    public void init() {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup(1);
        bootstrap.group(group);
        MessageEncoder encoder = new MessageEncoder();
        collector = new ClientMessageCollector(registry, this);
        bootstrap.channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipe = ch.pipeline();
                pipe.addLast(new ReadTimeoutHandler(60));
                pipe.addLast(new MessageDecoder());
                pipe.addLast(encoder);
                pipe.addLast(collector);
            }

        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true);
    }

    public void connect() {
        bootstrap.connect(ip, port).syncUninterruptibly();
    }

    public void reconnect() {
        if (stopped) {
            return;
        }
        bootstrap.connect(ip, port).addListener(future -> {
            if (future.isSuccess()) {
                return;
            }
            if (!stopped) {
                group.schedule(() -> {
                    reconnect();
                }, 1, TimeUnit.SECONDS);
            }
            LOG.error("connect {}:{} failure", ip, port, future.cause());
        });
    }

    public void close() {
        stopped = true;
        collector.close();
        group.shutdownGracefully(0, 5000, TimeUnit.SECONDS);
    }

}
