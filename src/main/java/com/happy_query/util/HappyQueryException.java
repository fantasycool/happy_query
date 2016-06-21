package com.happy_query.util;

/**
 * Created by frio on 16/6/15.
 */
public class HappyQueryException extends RuntimeException {
    public HappyQueryException(String s, Exception e) {
        super(s, e);
    }

    public HappyQueryException(Exception e) {
        super(e);
    }

    public HappyQueryException(String s) {
        super(s);
    }
}
