package com.happ_query.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.happy_query.parser.dao.DataDefinitionDao;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.query.Query;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by frio on 16/6/22.
 */
public class DaoTest {
    private DataSource dataSource;

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
    }

    @Test
    public void testQueryBySubType() {
       List<DataDefinition> list =  DataDefinitionDao.queryBySubType(dataSource, "病史记录");
        for(DataDefinition d : list){
            System.out.println(d.toString());
        }
    }
}
