package com.happ_query.test;

import com.happy_query.cache.CacheManager;
import com.happy_query.parser.IJsonLogicParser;
import com.happy_query.parser.IJsonSqlParser;
import com.happy_query.parser.JsonLogicParser;
import com.happy_query.parser.JsqlSqlParser;
import com.happy_query.parser.definition.DataDefinition;
import com.happy_query.parser.definition.DataDefinitionDataType;
import com.happy_query.parser.domain.JsonParseDataParam;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

/**
 * Created by frio on 16/6/16.
 */
public class JSqlParserTest {
    @Test
    public void testGenerateQuerySql() throws ExecutionException {
        CacheManager cacheManager = new CacheManager(null);
        DataDefinition d1 =  DataDefinition.createDataDefinitionById(1l);
        d1.setDataType(DataDefinitionDataType.BOOLEAN);
        cacheManager.putValue(DataDefinition.createDataDefinitionById(1l), d1);

        DataDefinition d2 =  DataDefinition.createDataDefinitionById(2l);
        d1.setDataType(DataDefinitionDataType.DATETIME);
        cacheManager.putValue(DataDefinition.createDataDefinitionById(2l), d2);

        DataDefinition d3 =  DataDefinition.createDataDefinitionById(3l);
        d1.setDataType(DataDefinitionDataType.STRING);
        cacheManager.putValue(DataDefinition.createDataDefinitionById(3l), d3);

        DataDefinition d4 =  DataDefinition.createDataDefinitionById(4l);
        d1.setDataType(DataDefinitionDataType.INT);
        cacheManager.putValue(DataDefinition.createDataDefinitionById(4l), d4);
        IJsonLogicParser jsonLogicParser = new JsonLogicParser(cacheManager);
        String j = "[\n" +
                "      \"and\",\n" +
                "          {\n" +
                "              \"attr\":\"1\",\n" +
                "              \"operator\":\">\",\n" +
                "              \"value\":\"1\"\n" +
                "          },\n" +
                "          {\n" +
                "              \"attr\":\"2\",\n" +
                "              \"operator\": \"<\",\n" +
                "              \"value\":\"10\"\n" +
                "          },\n" +
                "          [\n" +
                "              \"or\",\n" +
                "                  {\n" +
                "                      \"attr\":\"3\",\n" +
                "                      \"operator\":\"in\",\n" +
                "                      \"value\":\"(1,2,3,4)\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                      \"attr\":\"4\",\n" +
                "                      \"operator\":\"=\",\n" +
                "                      \"value\":\"10\"\n" +
                "                  }\n" +
                "          ]\n" +
                "  ]";
        IJsonSqlParser jsqlSqlParser = new JsqlSqlParser(jsonLogicParser);
        JsonParseDataParam jsonParseDataParam = new JsonParseDataParam();
        jsonParseDataParam.setJsonOperation(j);
        jsonParseDataParam.setLeftPrimaryId("id");
        jsonParseDataParam.setLeftTableName("left_table");
        jsonParseDataParam.setLimitStart(0);
        jsonParseDataParam.setSize(30);
        jsonParseDataParam.setRightTableName("data_definition_value");
        System.out.println(jsqlSqlParser.convertJsonLogicToCountSql(jsonParseDataParam));
    }

    @Test
    public void testGenerateCountSql(){

    }
}
