package com.happ_query.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.happy_query.domain.DataDefinition;
import com.happy_query.query.cache.DataDefinitionCacheManager;
import com.happy_query.query.cache.RelationCacheManager;
import com.happy_query.parser.JsonSqlParser;
import com.happy_query.query.Query;
import com.happy_query.writer.Writer;
import org.junit.Before;
import org.junit.Test;

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
        dd.setUrl("jdbc:mysql://120.27.234.146:3306/prm?characterEncoding=utf8&allowMultiQueries=true");
        dd.setUsername("qa");
        dd.setPassword("qa");
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

    @Test
    public void test(){
        DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition("1478674759465");
        System.out.println();
    }
}
