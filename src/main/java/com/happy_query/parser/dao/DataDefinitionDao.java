package com.happy_query.parser.dao;

import com.happy_query.parser.definition.DataDefinition;
import com.happy_query.query.domain.Row;
import com.happy_query.util.JDBCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
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
        if(id == null){
            return null;
        }
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
        Map<String, Object> m = new HashMap<String, Object>();
        for(Map.Entry<String, Row.Value> entry : data.get(0).entrySet()){
            m.put(entry.getKey(), entry.getValue().getValue());
        }
        return m;
    }

    public static List<Long> insertDataDefinition(DataSource dataSource, DataDefinition dataDefinition){
        return null;
    }

}
