package com.happy_query.domain;

import com.alibaba.fastjson.JSON;
import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.ReflectionUtil;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by frio on 16/10/31.
 */
public class PrmTagKeyRelation {
    private Long id;
    private String groupKey;
    private String subKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getSubKey() {
        return subKey;
    }

    public void setSubKey(String subKey) {
        this.subKey = subKey;
    }

    public static long insert(DataSource dataSource, PrmTagKeyRelation prmTagKeyRelation){
        Map<String, Object> map = ReflectionUtil.cloneBeanToMap(prmTagKeyRelation);
        try {
            return JDBCUtils.insertToTable(dataSource, Constant.PRM_TAG_KEY_RELATION, map);
        } catch (SQLException e) {
            throw new HappyQueryException("insert prm tag key relation failed!prmTagKeyRelation:"
                    + JSON.toJSONString(prmTagKeyRelation), e);
        }
    }

    /**
     * 根据组标签的key查询子标签的key列表
     * @param dataSource
     * @param groupKey
     * @return
     */
    public static List<String> querySubKeysByGroupKey(DataSource dataSource, String groupKey){
        List<Object> params = new ArrayList<>();
        params.add(groupKey);
        try {
            List<Map<String, Object>> list = JDBCUtils.executeQuery(dataSource, "select sub_key from " + Constant.PRM_TAG_KEY_RELATION + " where group_key=?", params);
            List<String> result = new ArrayList<>();
            for(Map<String, Object> m : list){
                result.add(m.get("sub_key").toString());
            }
            return result;
        } catch (SQLException e) {
            throw new HappyQueryException("queyr tag key relation failed!groupKey:"
                    + groupKey, e);
        }
    }
}
