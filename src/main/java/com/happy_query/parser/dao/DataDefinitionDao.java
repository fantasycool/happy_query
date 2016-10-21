package com.happy_query.parser.dao;

import com.happy_query.cache.DataDefinitionCacheManager;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.NullChecker;
import com.happy_query.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * hate heavy and big orm
 * like clean jdbc call
 * Created by frio on 16/6/15.
 */
public abstract class DataDefinitionDao {
    static Logger LOG = LoggerFactory.getLogger(DataDefinitionDao.class);
    public static String TABLE_NAME = "statistic_keys";

    public static DataDefinition getDataDefinition(DataSource dataSource, Long id) {
        NullChecker.checkNull(id);
        List<Object> list = Arrays.asList((Object) id);
        try {
            String sql = String.format("select * from %s where id=? and status=0 order by gmt_create desc limit 1", TABLE_NAME);
            List<Map<String, Object>> data = JDBCUtils.executeQuery(dataSource, sql, list);
            return getDataDefinition(data);
        } catch (SQLException e) {
            LOG.error("getDataDefinition failed!", e);
            LOG.error("param id is [{}]", id);
        }
        return new DataDefinitionCacheManager.NullDataDefinition();
    }

    private static DataDefinition getDataDefinition(List<Map<String, Object>> data) {
        if (data == null || data.size() == 0) {
            return new DataDefinitionCacheManager.NullDataDefinition();
        }
        DataDefinition dataDefinition = new DataDefinition();
        ReflectionUtil.cloneMapValueToBean(data.get(0), dataDefinition);
        return dataDefinition;
    }

    public static DataDefinition getDataDefinitionByName(DataSource dataSource, String name) {
        NullChecker.checkNull(name);
        List<Object> list = Arrays.asList((Object) name);
        try {
            String sql = String.format("select * from %s where `key`=? and status=0 order by gmt_create desc limit 1", TABLE_NAME);
            List<Map<String, Object>> data = JDBCUtils.executeQuery(dataSource, sql, list);
            return getDataDefinition(data);
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
            String sql = String.format("select * from %s where (nick_name=? or name=?) and status=0 order by gmt_create desc limit 1", TABLE_NAME);
            List<Map<String, Object>> data = JDBCUtils.executeQuery(dataSource, sql, list);
            return getDataDefinition(data);
        } catch (SQLException e) {
            LOG.error("getDataDefinitionByNickName failed!param name is [{}]", name, e);
            throw new HappyQueryException("getDataDefinitionByName failed!param name is " + name, e);
        }
    }


    public static void insertDataDefinition(DataSource dataSource, DataDefinition dataDefinition) {
        NullChecker.checkNull(dataDefinition);
        Map<String, Object> map = ReflectionUtil.cloneBeanToMap(dataDefinition);
        try {
            dataDefinition.setId(JDBCUtils.insertToTable(dataSource, TABLE_NAME, map));
        } catch (SQLException e) {
            LOG.error("insert datadefinition failed, datadefinition content is:[{}], t is:[{}]", dataDefinition.toString(), e);
            throw new HappyQueryException("insert failed", e);
        }
    }

    public static int updateDataDefinition(DataSource dataSource, DataDefinition dataDefinition) {
        NullChecker.checkNull(dataDefinition);
        NullChecker.checkNull(dataDefinition.getId());
        Map<String, Object> map = ReflectionUtil.cloneBeanToMap(dataDefinition);
        try {
            return JDBCUtils.executeUpdateById(dataSource, TABLE_NAME, map, "id", dataDefinition.getId());
        } catch (SQLException e) {
            LOG.error("update datadefinition failed, datadefinition content is:[{}], t is:[{}]", dataDefinition.toString(), e);
            throw new HappyQueryException("update failed", e);
        }
    }

    public static void setDataDefinitionTableName(String ddName){
        TABLE_NAME = ddName;
    }
}
