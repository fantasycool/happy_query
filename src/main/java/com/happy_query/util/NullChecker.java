package com.happy_query.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by frio on 16/6/16.
 */
public class NullChecker {
    public static void checkNull(Object arg){
        if(arg == null){
            throw new NullPointerException("arg can't be null");
        }
    }

    public static void checkNull(Object... arg){
        for(Object o : arg){
            if(o != null && o instanceof String){
                if(StringUtils.isBlank(o.toString())){
                    throw new IllegalArgumentException();
                }
            }
            if(o == null){
                throw new IllegalArgumentException();
            }
        }
    }
}
