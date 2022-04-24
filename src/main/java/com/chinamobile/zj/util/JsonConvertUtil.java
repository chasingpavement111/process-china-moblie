package com.chinamobile.zj.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * 统一序列化方法，避免fastjson、Jackson混用
 * 规范序列化方法，如泛型
 */
public class JsonConvertUtil {

    public static  <T> T parseToObject(String jsonString, Class<T> clazz) {
        return JSON.parseObject(jsonString, clazz);
    }

    public static  <T> T parseToParameterizedType(String jsonString, TypeReference<T> type) {
        return JSON.parseObject(jsonString, type);
    }

    public static String toJsonString(Object obj) {
        return JSON.toJSONString(obj);
    }
}
