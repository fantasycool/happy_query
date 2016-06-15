package com.happy_query.parser.dao;

import com.happy_query.parser.definition.DataDefinition;
import com.happy_query.util.JDBCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * refer to Elco's words:
 * nice quick initial development, and a big drain on your resources further on in the project
 * when tracking ORM related bugs and inefficiencies. I also hate the fact that it seems to give
 * developers the idea that they never have to write specific optimized queries
 * <p>
 * Created by frio on 16/6/15.
 */
public class DataDefinitionDao {
    static Logger LOG = LoggerFactory.getLogger(DataDefinitionDao.class);

    public static DataDefinition getDataDefinition(DataSource dataSource, Long id) {
        List<Object> list = Arrays.asList((Object) id);
        try {
            List<Map<String, Object>> data = JDBCUtils.executeQuery(dataSource, "select * from data_definition where id=? order by gmt_create desc limit 1", list);
            return DataDefinition.createFromMapData(data.get(0));
        } catch (SQLException e) {
            LOG.error("getDataDefinition failed!", e);
            LOG.error("param id is [{}]", id);
        }
        return null;
    }
}
