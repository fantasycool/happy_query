package com.happy_query.query.domain;

import com.happy_query.parser.domain.JsonParseDataParam;

import java.util.List;
import java.util.Map;

/**
 * Created by frio on 16/6/15.
 */
public class QueryResult {
    private List<Row> rows;
    private Integer count;
    private JsonParseDataParam jsonParseDataParam;
    private String querySql;

    public static QueryResult createFromOrinalData(JsonParseDataParam jsonParseDataParam, List<Map<String, Object>> originalQueryResult, List<Map<String, Object>> countQueryResult){

        return null;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
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
