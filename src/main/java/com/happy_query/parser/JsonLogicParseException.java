package com.happy_query.parser;

import java.util.concurrent.ExecutionException;

/**
 * Created by frio on 16/6/14.
 */
public class JsonLogicParseException extends RuntimeException {
    public JsonLogicParseException(String message) {
        super(message);
    }

    public JsonLogicParseException(String format, ExecutionException e) {
        super(format, e);
    }
}
