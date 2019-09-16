package com.death00.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author death00
 * @date 2019/9/16
 */
@Getter
@Setter
@Builder
public class MessageOutput {

    private String requestId;

    private String type;

    /**
     * 返回结果
     */
    private Object response;
}
