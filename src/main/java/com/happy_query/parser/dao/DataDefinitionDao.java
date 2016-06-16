package com.happy_query.parser.dao;

import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.query.domain.Row;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.NullChecker;
import com.happy_query.writer.HappyWriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hate heavy and big orm
 * like clean jdbc call
 * Created by frio on 16/6/15.
 */
public class DataDefinitionDao {
    static Logger LOG = LoggerFactory.getLogger(DataDefinitionDao.class);
    public static String TABLE_NAME = "data_definition";

    public static DataDefinition getDataDefinition(DataSource dataSource, Long id) {
        NullChecker.checkNull(id);
        List<Object> list = Arrays.asList((Object) id);
        try {
            List<Map<String, Row.Value>> data = JDBCUtils.executeQuery(dataSource, "select * from data_definition where id=? order by gmt_create desc limit 1", list);
            Map<String, Object> m = convertFromValueMap(data);
            return DataDefinition.createFromMapData(m);
        } catch (SQLException e) {
            LOG.error("getDataDefinition failed!", e);
            LOG.error("param id is [{}]", id);
        }
        return null;
    }

    private static Map<String, Object> convertFromValueMap(List<Map<String, Row.Value>> data) {
        NullChecker.checkNull(data);
        Map<String, Object> m = new HashMap<String, Object>();
        for (Map.Entry<String, Row.Value> entry : data.get(0).entrySet()) {
            m.put(entry.getKey(), entry.getValue().getValue());
        }
        return m;
    }

    public static void insertDataDefinition(DataSource dataSource, DataDefinition dataDefinition) {
        NullChecker.checkNull(dataDefinition);
        Map<String, Object> map = dataDefinition.inverseDataDefinition();
        try {
            JDBCUtils.insertToTable(dataSource, TABLE_NAME, map);
        } catch (SQLException e) {
            LOG.error("insert datadefinition failed, datadefinition content is:[{}], t is:[{}]", dataDefinition.toString(), e);
            throw new HappyWriterException("insert failed", e);
        }
    }

    public static int updateDataDefinition(DataSource dataSource, DataDefinition dataDefinition) {
        NullChecker.checkNull(dataDefinition);
        NullChecker.checkNull(dataDefinition.getId());
        try {
            return JDBCUtils.executeUpdateById(dataSource, TABLE_NAME, dataDefinition.inverseDataDefinition(), "id", dataDefinition.getId());
        } catch (SQLException e) {
            LOG.error("update datadefinition failed, datadefinition content is:[{}], t is:[{}]", dataDefinition.toString(), e);
            throw new HappyWriterException("update failed", e);
        }

    }

}
