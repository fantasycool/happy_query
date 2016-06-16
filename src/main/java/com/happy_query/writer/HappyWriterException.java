package com.happy_query.writer;

/**
 * Created by frio on 16/6/16.
 */
public class HappyWriterException extends RuntimeException{
    public HappyWriterException() {
        super();
    }

    public HappyWriterException(String message) {
        super(message);
    }

    public HappyWriterException(String message, Throwable cause) {
        super(message, cause);
    }

    public HappyWriterException(Throwable cause) {
        super(cause);
    }

    protected HappyWriterException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
