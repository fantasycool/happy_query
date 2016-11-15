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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
        dd.setUrl("jdbc:mysql://192.168.10.10:3306/prm?characterEncoding=utf8&allowMultiQueries=true");
        dd.setUsername("qa");
        dd.setPassword("qa@123");
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
        Map<String, Object> m = new HashMap<>();
        m.put("a", 1);
        m.put("b", 2);
        m.put("c", 3);
        for(Iterator<Map.Entry<String, Object>> it = m.entrySet().iterator(); it.hasNext() ;){
            Map.Entry<String, Object> entry = it.next();
                    it.remove();
        }
    }
}
