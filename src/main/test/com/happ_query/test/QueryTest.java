package com.happ_query.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.Query;
import com.happy_query.query.domain.QueryResult;
import com.happy_query.query.domain.Row;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by frio on 16/6/22.
 */
public class QueryTest {
    private DataSource dataSource;
    private Query query;
    @Before
    public void init() {
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
        dataSource = dd;
        query = new Query(dataSource);
    }

    /**
     * test query by id
     */

    @Test
    public void testQueryById(){
        QueryResult qr = query.queryByLeftId(613523l, "user");
        List<Row> rows = qr.getRows();
        for(Row r : rows){
            System.out.println(JSON.toJSONString(r.getFlatMapData()));
        }
    }

    @Test
    public void testQueryByJson(){
        String j = "[\n" +
                "      \"or\",\n" +
                "          {\n" +
                "              \"attr\":\"1\",\n" +
                "              \"operator\":\">\",\n" +
                "              \"value\":\"110\"\n" +
                "          },\n" +
                "          {\n" +
                "              \"attr\":\"1\",\n" +
                "              \"operator\": \"<\",\n" +
                "              \"value\":\"112\"\n" +
                "          },\n" +
                "          {\n" +
                "              \"attr\":\"2\",\n" +
                "              \"operator\":\"=\",\n" +
                "              \"value\":\"23\"\n" +
                "          },\n" +
                "          {\n" +
                "              \"attr\":\"3\",\n" +
                "              \"operator\":\"like\",\n" +
                "              \"value\":\"文%\"\n" +
                "          },\n" +
                "          {\n" +
                "              \"attr\":\"4\",\n" +
                "              \"operator\":\"in\",\n" +
                "              \"value\":[1,2,3,4]\n" +
                "          }\n" +
                "  ]";
        JsonParseDataParam jsonParseDataParam = new JsonParseDataParam();
        jsonParseDataParam.setJsonOperation(j);
        jsonParseDataParam.setLeftPrimaryId("id");
        jsonParseDataParam.setLeftTableName("left_table");
        jsonParseDataParam.setLimitStart(0);
        jsonParseDataParam.setSize(30);
        jsonParseDataParam.setRightTableName("data_definition_value");
        QueryResult queryResult = query.queryByJsonLogic(jsonParseDataParam);
        List<Row> rows = queryResult.getRows();
        for(Row r : rows){
            System.out.println("==========================new row coming======================");
            Long leftId = r.getLeftId();
            System.out.println("leftId is:" + leftId);
            System.out.println("==============================================================");
            System.out.println("leftTableData is :" + JSON.toJSONString(r.getLeftTableData()));
            System.out.println("==============================================================");
            System.out.println("datas is:" + JSON.toJSONString(r.getFlatMapData()));
        }
    }
}
