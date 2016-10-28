package com.happy_query.domain;

import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.NullChecker;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * 互相有关联影响的指标存储
 * Created by frio on 16/10/21.
 */
public class KeyRelation {
    static Logger LOG = LoggerFactory.getLogger(KeyRelation.class);
    private Long id;
    private String sourceKey;
    private String key;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 获取指标key的列表中会被关联更改的key dd的列表
     * @param key
     * @param dataSource
     * @return
     */
    public static Set<String> getAffectedRelationKeys(String key, DataSource dataSource){
        List<Object> params = new ArrayList<>();
        params.add(key);
        try {
            List<Map<String, Object>> list = JDBCUtils.executeQuery(dataSource,
                    String.format("select * from %s where source_key=?", Constant.KEY_RELATION_TABLE_NAME), params);
            if(null == list || list.size() == 0){
                return new HashSet<>();
            }
            Set<String> result = new HashSet<>();
            for(Map<String, Object> m : list){
                result.add(m.get("key").toString());
            }
            return result;
        } catch (SQLException e) {
            throw new HappyQueryException("key:" + key + " get failed!", e);
        }
    }

    /**
     * insert key relation,表示指标之间的互相影响关系
     * @param sourceKey
     * @param targetKey
     * @param dataSource
     * @return
     */
    public static long insertKeyRelation(String sourceKey, String targetKey, DataSource dataSource){
        NullChecker.checkNull(sourceKey, targetKey, dataSource);
        Map<String, Object> params = new HashMap<>();
        params.put("source_key", sourceKey);
        params.put("target_key", targetKey);
        try{
            return JDBCUtils.insertToTable(dataSource, Constant.KEY_RELATION_TABLE_NAME, params);
        }catch(MySQLIntegrityConstraintViolationException e){
            LOG.error("have already inserted key relation record", e);
            return 0l;
        }catch(SQLException e){
            throw new HappyQueryException("key relation insert failed, sourceKey:" + sourceKey + ";targetKey:" + targetKey, e);
        }
    }
}
