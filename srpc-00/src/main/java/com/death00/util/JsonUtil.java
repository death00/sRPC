package com.death00.util;

import com.google.gson.Gson;

/**
 * @author death00
 * @date 2019/9/16
 */
public class JsonUtil {

    private static final Gson GSON = new Gson();

    public static <T> T parseObject(String msg, Class<T> clazz) {
        return GSON.fromJson(msg, clazz);
    }

    public static String toJSONString(Object object) {
        return GSON.toJson(object);
    }
}
