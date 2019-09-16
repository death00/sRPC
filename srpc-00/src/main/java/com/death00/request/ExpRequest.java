package com.death00.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 计算幂
 *
 * @author death00
 * @date 2019/9/16
 */
@Getter
@Setter
@Builder
public class ExpRequest {

    /**
     * 底数
     */
    private int base;

    /**
     * 幂
     */
    private int exp;
}
