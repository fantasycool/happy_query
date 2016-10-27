package com.happ_query.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.happy_query.query.cache.DataDefinitionCacheManager;
import com.happy_query.query.cache.RelationCacheManager;
import com.happy_query.parser.JsonSqlParser;
import com.happy_query.query.Query;
import com.happy_query.writer.Writer;
import org.junit.Before;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by frio on 16/10/19.
 */
public class BaseTest {
    protected DataSource dataSource;
    protected JsonSqlParser jsonSqlParser;
    protected Query query;
    protected Writer writer;
    @Before
    public void init() {
        DruidDataSource dd = new DruidDataSource();
        dd.setUrl("jdbc:mysql://localhost/crucial");
        dd.setUsername("frio");
        dd.setPassword("frio");
        dd.setMaxActive(5);
        try {
            dd.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource = dd;
        DataDefinitionCacheManager.dataSource = dataSource;
        jsonSqlParser = new JsonSqlParser();
        DataDefinitionCacheManager.dataSource = dataSource;
        RelationCacheManager.dataSource = dataSource;
        query = new Query(dataSource);
        writer = new Writer(dataSource);
    }
}
