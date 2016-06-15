package com.happy_query.query.domain;

import com.happy_query.parser.definition.DataDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frio on 16/6/15.
 */
public class QueryResult {
    private Map<DataDefinition, Value> data = new HashMap<DataDefinition, Value>();

    public Map<DataDefinition, Value> getData() {
        return data;
    }

    public void setData(Map<DataDefinition, Value> data) {
        this.data = data;
    }

    static class Value {

    }
}
