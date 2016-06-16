package com.happy_query.util;

import freemarker.template.TemplateException;

/**
 * Created by frio on 16/6/15.
 */
public class QueryException extends RuntimeException {
    public QueryException(String s, Exception e) {
        super(s, e);
    }

    public QueryException(Exception e) {
        super(e);
    }
}
