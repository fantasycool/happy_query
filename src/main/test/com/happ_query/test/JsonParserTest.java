package com.happ_query.test;

import com.alibaba.fastjson.JSON;
import junit.framework.Assert;
import org.javatuples.Pair;
import org.junit.Test;

import java.util.List;

/**
 * Created by frio on 16/6/15.
 */
public class JsonParserTest extends BaseTest {
    @Test
    public void testContains() throws Exception {
        String json = "[\n" +
                "  \"and\",\n" +
                "  {\n" +
                "    \"attr\": \"dd1\",\n" +
                "    \"operator\":\"contains\",\n" +
                "    \"value\": [\"dd2\",\"dd3\",\"dd4\"]\n" +
                "  }\n" +
                "]\n";

        jsonSqlParser.convertJsonToQuerySql(json);
    }

    @Test
    public void testEqualsString() throws Exception {
        String json = "[\n" +
                "  \"and\",\n" +
                "  {\n" +
                "    \"attr\": \"dd2\",\n" +
                "    \"operator\":\"equals\",\n" +
                "    \"value\": \"abc\"\n" +
                "  }\n" +
                "]\n";
        Pair<String, List<String>> pair = jsonSqlParser.convertJsonToQuerySql(json);
        Assert.assertEquals(2, pair.getValue1());
    }

    @Test
    public void testRangeString() throws Exception {
        String json = "[\n" +
                "  \"and\",\n" +
                "  {\n" +
                "    \"attr\": \"dd5\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [10, 20]\n" +
                "  }\n" +
                "]\n";
        Pair<String, List<String>> p = jsonSqlParser.convertJsonToQuerySql(json);
        p.getValue1();
    }

    @Test
    public void testRangeDoubleString() throws Exception {
        String json = "[\n" +
                "  \"and\",\n" +
                "  {\n" +
                "    \"attr\": \"dd5\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [10.01, 20.2]\n" +
                "  }\n" +
                "]\n";
        Pair<String, List<String>> p = jsonSqlParser.convertJsonToQuerySql(json);
        System.out.println(JSON.toJSONString(p.getValue1()));
    }

    @Test
    public void testRangeDoubleStrString() throws Exception {
        String json = "[\n" +
                "  \"and\",\n" +
                "  {\n" +
                "    \"attr\": \"dd5\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"10.01\", \"20.2\"]\n" +
                "  }\n" +
                "]\n";
        Pair<String, List<String>> p = jsonSqlParser.convertJsonToQuerySql(json);
        System.out.println(JSON.toJSONString(p.getValue1()));
    }

    @Test
    public void testRangeOnlyEnd() throws Exception {
        String json = "[\n" +
                "  \"and\",\n" +
                "  {\n" +
                "    \"attr\": \"dd5\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"\", \"20.2\"]\n" +
                "  }\n" +
                "]\n";
        Pair<String, List<String>> p = jsonSqlParser.convertJsonToQuerySql(json);
        System.out.println(JSON.toJSONString(p.getValue1()));
    }

    @Test
    public void testRangeOnlyStart() throws Exception {
        String json = "[\n" +
                "  \"and\",\n" +
                "  {\n" +
                "    \"attr\": \"dd5\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"10\", \"\"]\n" +
                "  }\n" +
                "]\n";
        Pair<String, List<String>> p = jsonSqlParser.convertJsonToQuerySql(json);
        System.out.println(JSON.toJSONString(p.getValue1()));
    }

    @Test
    public void testMultipleExpression() throws Exception {
        String json = "[\n" +
                "  \"and\",\n" +
                "  {\n" +
                "    \"attr\": \"dd5\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"10\", \"\"]\n" +
                "  },\n" +
                "  {\n" +
                "    \"attr\": \"dd1\",\n" +
                "    \"operator\":\"contains\",\n" +
                "    \"value\": [\"dd2\",\"dd3\",\"dd4\"]\n" +
                "  },\n" +
                "  {\n" +
                "    \"attr\": \"dd2\",\n" +
                "    \"operator\":\"equals\",\n" +
                "    \"value\": \"abc\"\n" +
                "  }\n" +
                "]\n";
        Pair<String, List<String>> p = jsonSqlParser.convertJsonToQuerySql(json);
        System.out.println(JSON.toJSONString(p.getValue1()));
    }
}
