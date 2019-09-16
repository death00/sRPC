package com.death00.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author death00
 * @date 2019/9/16
 */
public class MessageRegistry {

    private Map<String, Class<?>> clazzes = new HashMap<>();

    public void register(String type, Class<?> clazz) {
        clazzes.put(type, clazz);
    }

    public Class<?> get(String type) {
        return clazzes.get(type);
    }
}
