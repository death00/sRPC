package com.death00.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author death00
 * @date 2019/9/16
 */
@FunctionalInterface
public interface IMessageHandler<T> {

    void handle(ChannelHandlerContext ctx, String requestId, T message);

}
