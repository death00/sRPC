package com.death00.util;

import java.util.UUID;

/**
 * @author death00
 * @date 2019/9/16
 */
public class RequestId {

    public static String next() {
        return UUID.randomUUID().toString();
    }

}
