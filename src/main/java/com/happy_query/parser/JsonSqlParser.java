package com.happy_query.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.happy_query.cache.DataDefinitionCacheManager;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.DataDefinitionDataType;
import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by frio on 16/6/14.
 */
public class JsonSqlParser implements IJsonSqlParser {

    @Override
    public Pair<String, List<String>> convertJsonToQuerySql(String json) {
        if (StringUtils.isBlank(json)) {
            throw new IllegalArgumentException();
        }
        List<String> resultQuerys = new ArrayList<>();
        StringBuilder prmUserInfoQueryStr = new StringBuilder();
        List<String> dataDefinitionValueQueryStr = new ArrayList<>();

        try {
            JSONArray jsonArray = (JSONArray) JSON.parse(json);
            String connector = jsonArray.getString(0);
            if (!connector.equals(Constant.AND) && !connector.equals(Constant.OR)) {
                throw new HappyQueryException("not support connector type:" + connector);
            }
            for (int i = 1; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String keyName = jsonObject.getString("attr");
                DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(keyName);
                if (dataDefinition instanceof DataDefinitionCacheManager.NullDataDefinition
                        ) {
                    throw new HappyQueryException("keyName:" + keyName + " dd not exists!");
                }
                if (jsonObject.getString(Constant.OPERATOR).equals(Constant.RANGE)) {
                    JSONArray value = jsonObject.getJSONArray(Constant.VALUE);
                    if (dataDefinition.getDataTypeEnum() == DataDefinitionDataType.INT
                            || dataDefinition.getDataTypeEnum() == DataDefinitionDataType.DOUBLE
                            || dataDefinition.getDataTypeEnum() == DataDefinitionDataType.FLOAT) {
                        String startValue = value.getString(0);
                        String endValue = value.getString(1);
                        if (StringUtils.isBlank(value.getString(0))) {
                            startValue = String.valueOf(Integer.MIN_VALUE);
                        }
                        if (StringUtils.isBlank(value.getString(1))) {
                            endValue = String.valueOf(Integer.MAX_VALUE);
                        }
                        String expression = String.format("(%s>=%s and %s<=%s)", keyName, startValue, keyName, endValue);
                        if (dataDefinition.getLeftData()) {
                            appendPrmUserInfoQueryStr(prmUserInfoQueryStr, connector, expression);
                        } else {
                            appendDataDefinitionValueQueryStrStartValueEndValue(dataDefinitionValueQueryStr, dataDefinition, startValue, endValue);
                        }
                    } else if(dataDefinition.getDataTypeEnum() == DataDefinitionDataType.DATETIME){
                        String startValue = getDataTimeIntStr(value.getString(0));
                        String endValue = getDataTimeIntStr(value.getString(1));
                        String expression = String.format("(%s>=%s and %s<=%s)", keyName, startValue, keyName, endValue);
                        if (dataDefinition.getLeftData()) {
                            appendPrmUserInfoQueryStr(prmUserInfoQueryStr, connector, expression);
                        } else {
                            appendDataDefinitionValueQueryStrStartValueEndValue(dataDefinitionValueQueryStr, dataDefinition, startValue, endValue);
                        }
                    } else {
                        throw new HappyQueryException("keyName:" + keyName + " not support range operator");
                    }
                } else if (jsonObject.getString(Constant.OPERATOR).equals(Constant.CONTAINS)) {
                    JSONArray value = jsonObject.getJSONArray(Constant.VALUE);
                    if(!dataDefinition.getDefinitionType().equals(Constant.MULTISELECT)){
                        throw new HappyQueryException("keyName:" + keyName + " have to be a multiselelct dd to support contains");
                    }
                    for(Object k : value){
                        DataDefinition childMultiSelect = DataDefinitionCacheManager.getDataDefinition(k);
                        if(childMultiSelect instanceof DataDefinitionCacheManager.NullDataDefinition){
                            throw new HappyQueryException("keyName:" + keyName + " arg option:" + k + " is not a dd");
                        }
                        appendDataDefinitionValueQueryStrMultiSelect(dataDefinitionValueQueryStr, childMultiSelect);
                    }
                } else if (jsonObject.getString(Constant.OPERATOR).equals(Constant.EQUALS)) {
                    String value = jsonObject.getString(Constant.VALUE);
                    if(dataDefinition.getLeftData()){
                        String expression = String.format("(%s=%s)", keyName, value);
                        appendPrmUserInfoQueryStr(prmUserInfoQueryStr, connector, expression);
                    }else{
                        appendDataDefinitionValueQueryStrEquals(dataDefinitionValueQueryStr, dataDefinition, value);
                    }
                }
            }
            resultQuerys.add(prmUserInfoQueryStr.toString());
            resultQuerys.addAll(dataDefinitionValueQueryStr);
            Pair<String, List<String>> pair = new Pair<>(connector, resultQuerys);
            return pair;
        } catch (Exception e) {
            throw new HappyQueryException("json parse failed, json is:" + json, e);
        }
    }

    /**
     * 多选选项筛选条件拼装
     * @param dataDefinitionValueQueryStr
     * @param childMultiSelect
     */
    private void appendDataDefinitionValueQueryStrMultiSelect(List<String> dataDefinitionValueQueryStr, DataDefinition childMultiSelect) {
        String columeName = childMultiSelect.getValueColumnName();
        dataDefinitionValueQueryStr.add(String.format("(#{prefix}dd_ref_id=%d and #{prefix}%s=\"1\")", childMultiSelect.getId(), columeName));
    }

    private void appendDataDefinitionValueQueryStrEquals(List<String> dataDefinitionValueQueryStr, DataDefinition dataDefinition, Object v){
        String columnName = dataDefinition.getValueColumnName();
        dataDefinitionValueQueryStr.add(String.format("(#{prefix}dd_ref_id=%d and #{prefix}%s=\"%s\")", dataDefinition.getId(), columnName, v.toString()));
    }

    private String getDataTimeIntStr(String string) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = simpleDateFormat.parse(string);
            return String.valueOf(d.getTime());
        } catch (ParseException e) {
            throw new HappyQueryException("%s is not a valid time str", e);
        }
    }

    /**
     * 起始筛选条件拼装
     * @param dataDefinitionValueQueryStr
     * @param dataDefinition
     * @param startValue
     * @param endValue
     */
    private void appendDataDefinitionValueQueryStrStartValueEndValue(List<String> dataDefinitionValueQueryStr, DataDefinition dataDefinition, String startValue, String endValue) {
        String columnName = dataDefinition.getValueColumnName();
        dataDefinitionValueQueryStr.add(String.format("(#{prefix}dd_ref_id=%d and #{prefix}%s>=%s and #{prefix}%s<=%s)", dataDefinition.getId(), columnName, startValue, columnName, endValue));
    }

    private void appendPrmUserInfoQueryStr(StringBuilder prmUserInfoQueryStr,
                                           String connector, String expression) {
        if(prmUserInfoQueryStr.length() == 0){
            prmUserInfoQueryStr.append(expression);
        }else{
            prmUserInfoQueryStr.append(" "+connector+ " ").append(expression);
        }
    }
}
