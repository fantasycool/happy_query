package com.happ_query.test;

import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.DataDefinitionDataType;
import com.happy_query.parser.domain.DefinitionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by frio on 16/6/17.
 */
public class MockUtil {
    public List<DataDefinition> getMockDataDefinitions(){
        List<DataDefinition> resultList = new ArrayList<DataDefinition>();
        DataDefinition d1 = DataDefinition.createDataDefinitionById(1l);
        d1.setDataOptions(Arrays.asList("1:dataoption1;2:dataoption2;3:dataoption3;4:dataoption4".split(";")));
        d1.setName("指标1");
        d1.setDataType(DataDefinitionDataType.STRING);
        d1.setDefinitionType(DefinitionType.SELECT);
        d1.setDescription("指标1描述");
        d1.setEditable(true);
        d1.setStatus(0);
        return null;
    }
}
