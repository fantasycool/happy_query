package com.happ_query.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.happy_query.query.Query;
import com.happy_query.query.domain.QueryResult;
import com.happy_query.query.domain.Row;
import com.happy_query.writer.Writer;
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
}
