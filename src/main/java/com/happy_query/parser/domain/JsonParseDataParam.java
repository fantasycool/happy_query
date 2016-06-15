package com.happy_query.parser.domain;

import java.util.List;
import java.util.Map;

/**
 * Created by frio on 16/6/15.
 */
public class JsonParseDataParam {
    /**
     * Json表达式
     */
    private String jsonOperation;

    private String leftTableName;

    private String rightTableName;

    private Map<String, String> contextParameters;

    private List<String> leftColumns;


    /**
     * limit n1, n2: n1
     */
    private int limitStart;

    /**
     * limit n1, n2: n2
     */
    private int size;

    public JsonParseDataParam(String jsonOperation, String leftTableName, String rightTableName
            , Map<String, String> contextParams, List<String> leftColumns, List<String> rightColumns) {
        this.jsonOperation = jsonOperation;
        this.leftTableName = leftTableName;
        this.rightTableName = rightTableName;
        this.contextParameters = contextParams;
        this.leftColumns = leftColumns;
    }

    public String getJsonOperation() {
        return jsonOperation;
    }

    public void setJsonOperation(String jsonOperation) {
        this.jsonOperation = jsonOperation;
    }

    public String getLeftTableName() {
        return leftTableName;
    }

    public void setLeftTableName(String leftTableName) {
        this.leftTableName = leftTableName;
    }

    public String getRightTableName() {
        return rightTableName;
    }

    public void setRightTableName(String rightTableName) {
        this.rightTableName = rightTableName;
    }

    public Map<String, String> getContextParameters() {
        return contextParameters;
    }

    public void setContextParameters(Map<String, String> contextParameters) {
        this.contextParameters = contextParameters;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<String> getLeftColumns() {
        return leftColumns;
    }

    public void setLeftColumns(List<String> leftColumns) {
        this.leftColumns = leftColumns;
    }
}
