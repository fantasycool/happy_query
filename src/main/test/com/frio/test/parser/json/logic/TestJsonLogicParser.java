package com.frio.test.parser.json.logic;

import com.frio.parser.json.logic.IJsonLogicParser;
import com.frio.parser.json.logic.JsonLogicParser;
import org.junit.Test;

/**
 * Created by frio on 16/6/14.
 */
public class TestJsonLogicParser {
    @Test
    public void testMethod() {
        IJsonLogicParser jsonLogicParser = new JsonLogicParser();
        String j = "[\n" +
                "      \"and\",\n" +
                "          {\n" +
                "              \"attr\":\"a\",\n" +
                "              \"operator\":\">\",\n" +
                "              \"value\":\"1\"\n" +
                "          },\n" +
                "          {\n" +
                "              \"attr\":\"b\",\n" +
                "              \"operator\": \"<\",\n" +
                "              \"value\":\"10\"\n" +
                "          },\n" +
                "          [\n" +
                "              \"or\",\n" +
                "                  {\n" +
                "                      \"attr\":\"c\",\n" +
                "                      \"operator\":\"in\",\n" +
                "                      \"value\":\"(1,2,3,4)\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                      \"attr\":\"d\",\n" +
                "                      \"operator\":\"=\",\n" +
                "                      \"value\":\"10\"\n" +
                "                  }\n" +
                "          ]\n" +
                "  ]";
        System.out.println(jsonLogicParser.convertJsonToLogicExpression(j, null, null));
    }
}
