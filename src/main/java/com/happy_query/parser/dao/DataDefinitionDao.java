package com.happy_query.parser.dao;

import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.query.domain.Row;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.NullChecker;
import com.happy_query.writer.HappyWriterException;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * hate heavy and big orm
 * like clean jdbc call
 * Created by frio on 16/6/15.
 */
public abstract class DataDefinitionDao {
    static Logger LOG = LoggerFactory.getLogger(DataDefinitionDao.class);
    public static String TABLE_NAME = "data_definition";

    public static DataDefinition getDataDefinition(DataSource dataSource, Long id) {
        NullChecker.checkNull(id);
        List<Object> list = Arrays.asList((Object) id);
        try {
            List<Map<String, Row.Value>> data = JDBCUtils.executeQuery(dataSource, "select * from data_definition where id=? order by gmt_create desc limit 1", list);
            if(data == null || data.size() == 0){
                return null;
            }
            Map<String, Object> m = convertFromValueMap(data.get(0));
            return DataDefinition.createFromMapData(m);
        } catch (SQLException e) {
            LOG.error("getDataDefinition failed!", e);
            LOG.error("param id is [{}]", id);
        }
        return null;
    }

    public static DataDefinition getDataDefinitionByName(DataSource dataSource, String name) {
        NullChecker.checkNull(name);
        List<Object> list = Arrays.asList((Object) name);
        try {
            List<Map<String, Row.Value>> data = JDBCUtils.executeQuery(dataSource, "select * from data_definition where name=? order by gmt_create desc limit 1", list);
            if(data.size() == 0){
                return null;
            }
            Map<String, Object> m = convertFromValueMap(data.get(0));
            return DataDefinition.createFromMapData(m);
        } catch (SQLException e) {
            LOG.error("getDataDefinitionByName failed!param name is [{}]", name, e);
            throw new HappyQueryException("getDataDefinitionByName failed!param name is " + name, e);
        }
    }

    public static DataDefinition getDataDefinitionByNickName(DataSource dataSource, String name) {
        NullChecker.checkNull(name);
        List<Object> list = new ArrayList();
        list.add(name);
        list.add(name);
        try {
            List<Map<String, Row.Value>> data = JDBCUtils.executeQuery(dataSource, "select * from data_definition where nick_name=? or name=? order by gmt_create desc limit 1", list);
            if(data.size() == 0){
                return null;
            }
            Map<String, Object> m = convertFromValueMap(data.get(0));
            return DataDefinition.createFromMapData(m);
        } catch (SQLException e) {
            LOG.error("getDataDefinitionByNickName failed!param name is [{}]", name, e);
            throw new HappyQueryException("getDataDefinitionByName failed!param name is " + name, e);
        }
    }

    public static List<DataDefinition> queryBySubType(DataSource dataSource, String subType) {
        NullChecker.checkNull(dataSource);
        NullChecker.checkNull(subType);
        List<Object> list = Arrays.asList((Object) (subType + "%"));
        try {
            List<Map<String, Row.Value>> datas = JDBCUtils.executeQuery(dataSource, "select * from data_definition where sub_type like ? order by gmt_create desc limit 1000", list);
            List<DataDefinition> dds = new ArrayList<DataDefinition>();
            for (Map<String, Row.Value> d : datas) {
                Map<String, Object> m = convertFromValueMap(d);
                DataDefinition dd = DataDefinition.createFromMapData(m);
                dds.add(dd);
            }
            return dds;
        } catch (SQLException e) {
            LOG.error("queryBySubType failed!subType is [{}]", subType, e);
            throw new HappyQueryException("queryBySubType failed!", e);
        }
    }

    private static Map<String, Object> convertFromValueMap(Map<String, Row.Value> data) {
        NullChecker.checkNull(data);
        Map<String, Object> m = new HashMap<String, Object>();
        for (Map.Entry<String, Row.Value> entry : data.entrySet()) {
            m.put(entry.getKey(), entry.getValue().getValue());
        }
        return m;
    }

    public static void insertDataDefinition(DataSource dataSource, DataDefinition dataDefinition) {
        NullChecker.checkNull(dataDefinition);
        Map<String, Object> map = dataDefinition.inverseDataDefinition();
        try {
            dataDefinition.setId(JDBCUtils.insertToTable(dataSource, TABLE_NAME, map));
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
