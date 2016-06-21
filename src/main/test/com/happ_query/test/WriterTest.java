package com.happ_query.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.happy_query.writer.Writer;
import com.happy_query.writer.domain.ImportParam;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;

/**
 * Created by frio on 16/6/21.
 */
public class WriterTest {
    private DataSource dataSource;
    private Writer writer;

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
        writer = new Writer();
        writer.setDataSource(dataSource);

    }

    @Test
    public void testImportDataByCSV() throws IOException {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/import_test.csv"));
        ImportParam importParam = new ImportParam("left_table", "id", "data_definition_value", reader, 0);
        writer.importDataByCSV(importParam);
    }
}
