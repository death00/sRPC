package com.death00.handler;

import com.death00.common.MessageOutput;
import com.death00.request.ExpRequest;
import com.death00.response.ExpResponse;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author death00
 * @date 2019/9/16
 */
public class ExpRequestHandler implements IMessageHandler<ExpRequest> {

    @Override
    public void handle(ChannelHandlerContext ctx, String requestId, ExpRequest message) {
        int base = message.getBase();
        int exp = message.getExp();
        long start = System.nanoTime();
        long res = 1;
        for (int i = 0; i < exp; i++) {
            res *= base;
        }
        long cost = System.nanoTime() - start;
        // 输出响应
        ctx.writeAndFlush(
                MessageOutput.builder()
                        .requestId(requestId)
                        .type("exp_res")
                        .response(ExpResponse.builder().value(res).costInNanos(cost).build())
                        .build()
        );
    }

}
