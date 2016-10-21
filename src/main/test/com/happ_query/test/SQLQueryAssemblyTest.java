package com.happ_query.test;

import com.happy_query.parser.SQLQueryAssembly;
import org.javatuples.Pair;
import org.junit.Test;

import java.util.List;

/**
 * Created by frio on 16/10/20.
 */
public class SQLQueryAssemblyTest extends BaseTest {

    @Test
    public void testTemplateParser(){
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
        System.out.println(SQLQueryAssembly.assemblyQuerySql(p, 0, 10, false));
    }
}
