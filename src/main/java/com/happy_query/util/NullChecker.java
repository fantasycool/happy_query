package com.happy_query.util;

/**
 * Created by frio on 16/6/16.
 */
public class NullChecker {
    public static void checkNull(Object arg){
        if(arg == null){
            throw new NullPointerException("arg can't be null");
        }
    }
}
