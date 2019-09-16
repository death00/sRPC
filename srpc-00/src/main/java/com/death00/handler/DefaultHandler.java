package com.death00.handler;

import com.death00.common.MessageInput;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author death00
 * @date 2019/9/16
 */
public class DefaultHandler implements IMessageHandler<MessageInput> {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, String requesetId, MessageInput input) {
        LOG.error("unrecognized message type {} comes", input.getType());
        ctx.close();
    }

}
