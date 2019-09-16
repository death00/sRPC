package com.death00.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 计算幂的结果
 *
 * @author death00
 * @date 2019/9/16
 */
@Getter
@Builder
public class ExpResponse {

    /**
     * 值
     */
    private long value;

    /**
     * 花费的时间
     */
    private long costInNanos;
}
