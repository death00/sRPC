package com.death00.common;

import com.death00.util.JsonUtil;
import lombok.Builder;
import lombok.Getter;

/**
 * @author death00
 * @date 2019/9/16
 */
@Getter
@Builder
public class MessageInput {

    private String type;

    private String requestId;

    private String payload;

    public <T> T getPayload(Class<T> clazz) {
        if (payload == null) {
            return null;
        }
        return JsonUtil.parseObject(payload, clazz);
    }
}
