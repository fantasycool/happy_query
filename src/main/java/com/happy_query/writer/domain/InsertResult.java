package com.happy_query.writer.domain;

import com.happy_query.query.domain.Row;

import java.util.List;

/**
 * result do to insert to happy query data data_definition_value
 * Created by frio on 16/6/20.
 */
public class InsertResult {
    private List<Row> rows;
    private String categoryType; //user,fuwu

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}
