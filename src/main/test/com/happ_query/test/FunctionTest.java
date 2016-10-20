package com.happ_query.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.happy_query.cache.DataDefinitionCacheManager;
import com.happy_query.util.Function;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by frio on 16/7/6.
 */
public class FunctionTest {

    @Before
    public void init(){
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
        DataDefinitionCacheManager.dataSource = dd;
    }

    @Test
    public void testReverseRender(){
        Function function = new Function();
//        function.render(Row.Value.createValue(null, "abc"), 2l, "detail");
    }

    @Test
    public void testRender(){
        Function function = new Function();
//        function.reverseRender("abc", 2l, "detail");
    }
}
