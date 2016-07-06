package com.happy_query.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by frio on 16/7/6.
 */
public class ReflectionUtil {
    static Logger LOG = LoggerFactory.getLogger(ReflectionUtil.class);

    public static Object invokeMethod(String methodName, Object object, Class<?>[] params, Object[] args) {
        try {
            Method method = object.getClass().getMethod(methodName, params);
            Object result = method.invoke(object, args);
            return result;
        } catch (Exception e) {
            LOG.error("call [{}] failed!", e);
            throw new HappyQueryException(e);
        }
    }
}
