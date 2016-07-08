package com.happy_query.writer;

/**
 * Created by frio on 16/6/16.
 */
public class HappyWriterException extends RuntimeException{
    private String code; //we use code to represent import line number
    private Throwable t;

    public HappyWriterException() {
        super();
    }

    public HappyWriterException(String message, String code){
        super(message);
        this.code = code;
    }

    public HappyWriterException(String message, String code, Throwable t){
        super(message, t);
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
