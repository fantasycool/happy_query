package com.happy_query.query.domain;

import com.happy_query.parser.domain.JsonParseDataParam;

/**
 * Created by frio on 16/6/15.
 */
public class QueryResult {
    private Row row;
    private Integer count;
    private JsonParseDataParam jsonParseDataParam;
    private String querySql;

    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public JsonParseDataParam getJsonParseDataParam() {
        return jsonParseDataParam;
    }

    public void setJsonParseDataParam(JsonParseDataParam jsonParseDataParam) {
        this.jsonParseDataParam = jsonParseDataParam;
    }

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }
}
