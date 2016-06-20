package com.happ_query.test;

import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.DataDefinitionDataType;
import com.happy_query.parser.domain.DefinitionType;
import com.happy_query.query.domain.Row;

import java.util.*;

/**
 * Created by frio on 16/6/17.
 */
public class MockUtil {
    public static List<DataDefinition> getMockDataDefinitions(){
        List<DataDefinition> resultList = new ArrayList<DataDefinition>();
        DataDefinition d1 = DataDefinition.createDataDefinitionById(1l);
        d1.setDataOptions(Arrays.asList("1:dataoption1;2:dataoption2;3:dataoption3;4:dataoption4".split(";")));
        d1.setName("指标1");
        d1.setDataType(DataDefinitionDataType.STRING);
        d1.setDefinitionType(DefinitionType.SELECT);
        d1.setDescription("指标1描述");
        d1.setEditable(true);
        d1.setStatus(0);
        d1.setUseTemplate(false);
        d1.setSubType("病史资料");
        d1.setTag(false);
        resultList.add(d1);

        d1 = DataDefinition.createDataDefinitionById(2l);
        d1.setName("指标2");
        d1.setDataType(DataDefinitionDataType.STRING);
        d1.setDefinitionType(DefinitionType.INPUT);
        d1.setDescription("指标2描述");
        d1.setEditable(true);
        d1.setStatus(0);
        d1.setUseTemplate(false);
        d1.setSubType("病史资料");
        d1.setTag(false);
        resultList.add(d1);

        d1 = DataDefinition.createDataDefinitionById(3l);
        d1.setName("指标3");
        d1.setDataType(DataDefinitionDataType.INT);
        d1.setDefinitionType(DefinitionType.INPUT);
        d1.setDescription("指标3描述");
        d1.setEditable(true);
        d1.setStatus(0);
        d1.setUseTemplate(false);
        d1.setSubType("病史资料");
        d1.setTag(false);
        resultList.add(d1);

        d1 = DataDefinition.createDataDefinitionById(4l);
        d1.setName("指标4");
        d1.setDataType(DataDefinitionDataType.DOUBLE);
        d1.setDefinitionType(DefinitionType.INPUT);
        d1.setDescription("指标4描述");
        d1.setEditable(true);
        d1.setStatus(0);
        d1.setUseTemplate(false);
        d1.setSubType("病史资料");
        d1.setTag(false);
        resultList.add(d1);

        d1 = DataDefinition.createDataDefinitionById(5l);
        d1.setName("指标5");
        d1.setDataType(DataDefinitionDataType.DOUBLE);
        d1.setDefinitionType(DefinitionType.CHECKBOX);
        d1.setDescription("指标5描述");
        d1.setEditable(true);
        d1.setStatus(0);
        d1.setUseTemplate(false);
        d1.setSubType("病史资料");
        d1.setTag(false);
        resultList.add(d1);

        d1 = DataDefinition.createDataDefinitionById(5l);
        d1.setName("指标6");
        d1.setDataType(DataDefinitionDataType.DOUBLE);
        d1.setDefinitionType(DefinitionType.CHECKBOX);
        d1.setDescription("指标6描述");
        d1.setEditable(true);
        d1.setStatus(0);
        d1.setUseTemplate(false);
        d1.setSubType("病史资料");
        d1.setTag(false);
        resultList.add(d1);

        return resultList;
    }

    public List<Row> mockRows(){
        List<DataDefinition> list = getMockDataDefinitions();
        List<Row> rows = new ArrayList<Row>();
        Map<DataDefinition, Row.Value> result = new HashMap<DataDefinition, Row.Value>();
        for(DataDefinition d : list){
            Row.Value v = new Row.Value();
            v.setDataDefinition(d);
            v.setValue(123213);
            result.put(d, v);
        }
        Row row = new Row();
        row.setData(result);
        rows.add(row);
        return rows;
    }
}
