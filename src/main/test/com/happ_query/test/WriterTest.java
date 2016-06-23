package com.happ_query.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.happy_query.query.Query;
import com.happy_query.writer.Writer;
import com.happy_query.writer.domain.ImportParam;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by frio on 16/6/21.
 */
public class WriterTest {
    private DataSource dataSource;
    private Writer writer;
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
        writer = new Writer();
        writer.setDataSource(dataSource);
        query = new Query(dataSource);

    }

    @Test
    public void testImportDataByCSV() throws IOException {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/import_test.csv"));
        ImportParam importParam = new ImportParam("left_table", "id", "data_definition_value", reader, 0);
        writer.importDataByCSV(importParam);
    }

    @Test
    public void testImportDataWithId() throws IOException {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/import_test1.csv"));
        ImportParam importParam = new ImportParam("left_table", "id", "data_definition_value", reader, 0);
        writer.importDataByCSV(importParam);
    }

    @Test
    public void testInsertDataWithLeftTable() throws IOException {
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/import_test_left.csv"));
        ImportParam importParam = new ImportParam("left_table", "id", "data_definition_value", reader, 0);
        writer.importDataByCSV(importParam);
    }

    @Test
    public void testDeleteRecord() throws IOException {
        writer.deleteRecord(613582, "user", "id");
    }

    @Test
    public void testUpdateRecord(){
        Map<Long, Object> updateMap = new HashMap<Long, Object>();
        updateMap.put(3l, "文本1");
        updateMap.put(6l, "adfafsfasfadsasf");
        writer.updateRecord(613581, "user", updateMap);
    }
}
