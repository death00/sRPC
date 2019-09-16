package com.death00.handler;

import com.death00.common.MessageOutput;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author death00
 * @date 2019/9/16
 */
public class FibRequestHandler implements IMessageHandler<Integer> {

    private List<Long> fibs = new ArrayList<>();

    {
        // fib(0) = 1
        fibs.add(1L);
        // fib(1) = 1
        fibs.add(1L);
    }

    @Override
    public void handle(ChannelHandlerContext ctx, String requestId, Integer n) {
        for (int i = fibs.size(); i < n + 1; i++) {
            long value = fibs.get(i - 2) + fibs.get(i - 1);
            fibs.add(value);
        }
        ctx.writeAndFlush(
                MessageOutput.builder()
                        .requestId(requestId)
                        .type("fib_res")
                        .response(fibs.get(n))
                        .build()
        );
    }

}
