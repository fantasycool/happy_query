package com.happy_query.query.domain;

import java.util.List;

/**
 * Created by frio on 16/6/15.
 */
public class HappyQueryResult {
    private List<Row> rows;
    private Integer count;

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
}
