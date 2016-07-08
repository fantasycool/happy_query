package com.happ_query.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.happy_query.cache.CacheManager;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.Query;
import com.happy_query.util.Function;
import com.happy_query.writer.Exporter;
import com.happy_query.writer.IExporter;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by frio on 16/7/7.
 */
public class ExportTest {
    private DataSource dataSource;
    private Query query;
    private IExporter exporter;
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
        exporter = new Exporter(query, new Function());
        CacheManager.dataSource = dd;
    }

    @Test
    public void testExport() throws ExecutionException, IOException {
        String j = "[\n" +
                "      \"and\",\n" +
                "          {\n" +
                "              \"attr\":\"1\",\n" +
                "              \"operator\":\">\",\n" +
                "              \"value\":\"1\"\n" +
                "          },\n" +
                "  ]";
        JsonParseDataParam jsonParseDataParam = new JsonParseDataParam();
        jsonParseDataParam.setJsonOperation(j);
        jsonParseDataParam.setLeftPrimaryId("cus_id");
        jsonParseDataParam.setLeftTableName("customer");
        jsonParseDataParam.setLimitStart(0);
        jsonParseDataParam.setSize(20);
        jsonParseDataParam.setRightTableName("data_definition_value");
        DataDefinition d1 = (DataDefinition) CacheManager.getValue(DataDefinition.createDataDefinitionById(1l));
        DataDefinition d2 = (DataDefinition) CacheManager.getValue(DataDefinition.createDataDefinitionById(2l));
        DataDefinition d3 = (DataDefinition) CacheManager.getValue(DataDefinition.createDataDefinitionById(3l));
        DataDefinition d4 = (DataDefinition) CacheManager.getValue(DataDefinition.createDataDefinitionById(4l));
        DataDefinition d5 = (DataDefinition) CacheManager.getValue(DataDefinition.createDataDefinitionById(5l));
        DataDefinition d6 = (DataDefinition) CacheManager.getValue(DataDefinition.createDataDefinitionById(6l));
        List<DataDefinition> args = new ArrayList<DataDefinition>();
        args.add(d1);
        args.add(d2);
        args.add(d3);
        args.add(d4);
        args.add(d5);
        args.add(d6);
        exporter.export(jsonParseDataParam, "/Users/frio/tmp/", "abc", args);
    }
}
