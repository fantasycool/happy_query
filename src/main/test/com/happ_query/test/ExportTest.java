package com.happ_query.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.happy_query.cache.DataDefinitionCacheManager;
import com.happy_query.query.Query;
import com.happy_query.writer.IExporter;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
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
//        exporter = new Exporter(query, new Function());
        DataDefinitionCacheManager.dataSource = dd;
    }

    @Test
    public void testExport() throws ExecutionException, IOException {
    }
}
