package com.happy_query.query.domain;

import com.happy_query.parser.definition.DataDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frio on 16/6/15.
 */
public class Row {
    /**
     * 字典表指标对应的字段
     */
    private Map<DataDefinition, Value> data = new HashMap<DataDefinition, Value>();
    /**
     * left table 对应的固有列的值
     */
    private Map<String, Value> leftTableData = new HashMap<String, Value>();

    public Map<DataDefinition, Value> getData() {
        return data;
    }

    public void setData(Map<DataDefinition, Value> data) {
        this.data = data;
    }

    public Map<String, Value> getLeftTableData() {
        return leftTableData;
    }

    public void setLeftTableData(Map<String, Value> leftTableData) {
        this.leftTableData = leftTableData;
    }

    public static class Value {
        DataDefinition dataDefinition;
        private String viewValue;
        private String columnName;
        //TODO
        /**
         * 读取template脚本,进行渲染
         * @return
         */
        public String getViewValue(){
            return "";
        }

        public DataDefinition getDataDefinition() {
            return dataDefinition;
        }

        public void setDataDefinition(DataDefinition dataDefinition) {
            this.dataDefinition = dataDefinition;
        }


        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }
    }
}
