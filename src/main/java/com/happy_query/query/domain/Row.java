package com.happy_query.query.domain;

import com.happy_query.cache.CacheManager;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.util.NullChecker;
import com.happy_query.util.TemplateUtil;
import com.happy_query.writer.HappyWriterException;
import javafx.scene.chart.PieChart;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by frio on 16/6/15.
 */
public class Row {
    static Logger LOG = LoggerFactory.getLogger(Row.class);
    /**
     * 字典表指标对应的字段
     */
    private Map<DataDefinition, Value> data = new HashMap<DataDefinition, Value>();
    /**
     * left table 对应的固有列的值 columnName->value
     */
    private Map<String, Value> leftTableData = new HashMap<String, Value>();
    /**
     * left table 對應Definition-> value
     */
    private Map<DataDefinition, Value> leftTableDefinitionDatas = new HashMap<DataDefinition, Value>();

    private List<String> leftTableDefinitionNames = new ArrayList<String>();

    /**
     * left table id value
     */
    private Long leftId;

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

    /**
     * 返回id->value的result
     * @return
     */
    public Map<Long, Value> getFlatMapData(){
        Map<Long, Value> result = new HashMap<Long, Value>();
        for(Map.Entry<DataDefinition, Value> e: data.entrySet()){
            result.put(e.getKey().getId(), e.getValue());
        }
        return result;
    }

    public List<String> getLeftTableDefinitionNames() {
        return leftTableDefinitionNames;
    }

    public void setLeftTableDefinitionNames(List<String> leftTableDefinitionNames) {
        this.leftTableDefinitionNames = leftTableDefinitionNames;
    }

    public static class Value {
        /**
         * right table use
         * when is left table datadefinitions, should be null
         */
        DataDefinition dataDefinition;
        private String viewValue;
        private String columnName;
        private int columnType; //refer to java.sql.Types
        /**
         * value object
         */
        Object value;

        private Value(){

        }

        /**
         * when creating value
         * generate view value with datadefinition template
         * @param dataDefinition
         * @param value
         * @return
         */
        public static Value createValue(DataDefinition dataDefinition, Object value){
            if(null == value){
                Value v = new Value();
                v.setDataDefinition(dataDefinition);
                v.setValue(value);
                return v;
            }
            Value v;
            if(null != dataDefinition){
                v = dataDefinition.formatStringValue(value.toString());
                if(StringUtils.isNoneBlank(dataDefinition.getTemplate())){
                    Map<String, Object> context = new HashMap<String, Object>();
                    context.put(dataDefinition.getTemplate(), value);
                    v.viewValue = TemplateUtil.getViewValueByTemplateStr(dataDefinition.getTemplate(), context);
                }
            }else {
                v = new Value();
                v.setValue(value);
            }
            return v;
        }


        /**
         * 读取template脚本,进行渲染
         * @return
         */
        public String getViewValue(){
            if(value == null){
                return "";
            }
            return value.toString();
        }

        public DataDefinition getDataDefinition() {
            return dataDefinition;
        }

        public void setDataDefinition(DataDefinition dataDefinition) {
            this.dataDefinition = dataDefinition;
        }

        public int getColumnType() {
            return columnType;
        }

        public void setColumnType(int columnType) {
            this.columnType = columnType;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public void setViewValue(String viewValue) {
            this.viewValue = viewValue;
        }
    }

    public Long getLeftId() {
        return leftId;
    }

    public void setLeftId(Long leftId) {
        this.leftId = leftId;
    }

    /**
     * create Row data from flatMapData {definitionId->value, ...}
     * @return
     */
    public static Row createFromFlatData(Map<Long, Object> flatMapData, long leftId){
        Map<String, Value> leftData = new HashMap<String, Value>();
        Map<DataDefinition, Value> datas = new HashMap<DataDefinition, Value>();
        Row row = new Row();
        List<String> leftTableDefinitionName = new ArrayList<String>();
        for(Long k : flatMapData.keySet()){
            try {
                DataDefinition dataDefinition = (DataDefinition) CacheManager.getValue(DataDefinition.createDataDefinitionById(k));
                if(dataDefinition == null){
                    LOG.error("we can't find the id");
                }
                if(dataDefinition.getLeftData()){
                    leftTableDefinitionName.add(dataDefinition.getName());
                    leftData.put(dataDefinition.getLefColName(), Value.createValue(dataDefinition, flatMapData.get(k)));
                }
                datas.put(dataDefinition, Value.createValue(dataDefinition, flatMapData.get(k)));
            } catch (ExecutionException e) {
                LOG.error("get defifination failed, id is [{}]", k, e);
                throw new HappyWriterException("get data defintion failed!", e);
            }
        }
        row.setLeftId(leftId);
        row.setLeftTableData(leftData);
        row.setData(datas);
        row.setLeftTableDefinitionNames(leftTableDefinitionName);
        return row;
    }

    public Map<DataDefinition, Value> getLeftTableDefinitionDatas() {
        return leftTableDefinitionDatas;
    }

    public void setLeftTableDefinitionDatas(Map<DataDefinition, Value> leftTableDefinitionDatas) {
        this.leftTableDefinitionDatas = leftTableDefinitionDatas;
    }
}
