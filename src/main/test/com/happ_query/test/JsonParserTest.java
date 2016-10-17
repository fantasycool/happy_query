package com.happ_query.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.happy_query.cache.CacheManager;
import com.happy_query.parser.IJsonLogicParser;
import com.happy_query.parser.JsonLogicParser;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.DataDefinitionDataType;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/**
 * Created by frio on 16/6/15.
 */
public class JsonParserTest {
    @Before
    public void init(){
        DruidDataSource dd = new DruidDataSource();
        dd.setUrl("jdbc:mysql://localhost/crucial");
        dd.setUsername("frio");
        dd.setPassword("fm7583165");
        dd.setMaxActive(5);
        try {
            dd.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CacheManager.dataSource = dd;
    }
    @Test
    public void testMethod() throws ExecutionException {
        DataDefinition d1 =  DataDefinition.createDataDefinitionById(1l);
        d1.setDataType(DataDefinitionDataType.BOOLEAN);
        CacheManager.putValue(DataDefinition.createDataDefinitionById(1l), d1);

        DataDefinition d2 =  DataDefinition.createDataDefinitionById(2l);
        d1.setDataType(DataDefinitionDataType.DATETIME);
        CacheManager.putValue(DataDefinition.createDataDefinitionById(2l), d2);

        DataDefinition d3 =  DataDefinition.createDataDefinitionById(3l);
        d1.setDataType(DataDefinitionDataType.INT);
        CacheManager.putValue(DataDefinition.createDataDefinitionById(3l), d3);

        DataDefinition d4 =  DataDefinition.createDataDefinitionById(4l);
        d1.setDataType(DataDefinitionDataType.STRING);
        CacheManager.putValue(DataDefinition.createDataDefinitionById(4l), d4);
        IJsonLogicParser jsonLogicParser = new JsonLogicParser();
//        String j = "[\n" +
//                "      \"and\",\n" +
//                "          {\n" +
//                "              \"attr\":\"1\",\n" +
//                "              \"operator\":\"=\",\n" +
//                "              \"value\":\"1970-11-11 11:11:11\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "              \"attr\":\"1\",\n" +
//                "              \"operator\": \"<=\",\n" +
//                "              \"value\":\"2010-11-11 00:00:00\"\n" +
//                "          },\n" +
//                "          [\n" +
//                "              \"or\",\n" +
//                "                  {\n" +
//                "                      \"attr\":\"3\",\n" +
//                "                      \"operator\":\"in\",\n" +
//                "                      \"value\":\"(1,2,3,4)\"\n" +
//                "                  },\n" +
//                "                  {\n" +
//                "                      \"attr\":\"4\",\n" +
//                "                      \"operator\":\"=\",\n" +
//                "                      \"value\":\"10\"\n" +
//                "                  }\n" +
//                "          ]\n" +
//                "  ]";
//        String j = "[\n" +
//                "      \"and\",\n" +
//                "          {\n" +
//                "              \"attr\":\"262\",\n" +
//                "              \"operator\":\">\",\n" +
//                "              \"value\":\"1469980800000\"\n" +
//                "          }\n" +
//                "  ]";
//        System.out.println(j);
        StringBuilder leftStringBuilder = new StringBuilder();
//        System.out.println(jsonLogicParser.convertJsonToLogicExpression(j, null, null, leftStringBuilder));
        System.out.println(leftStringBuilder.toString());
    }
}
