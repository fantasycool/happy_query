package com.happy_query.domain;

import com.happy_query.util.*;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by frio on 16/10/18.
 */
public class PrmUserInfo {
    static Logger LOG = LoggerFactory.getLogger(PrmUserInfo.class);
    private Long id;
    private String userKey;
    private String source;
    private Map<String, Object> datas;
    private List<DataDefinition> dds;
    private String empName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String, Object> getDatas() {
        return datas;
    }

    public void setDatas(Map<String, Object> datas) {
        this.datas = datas;
    }

    public List<DataDefinition> getDds() {
        return dds;
    }

    public void setDds(List<DataDefinition> dds) {
        this.dds = dds;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public static PrmUserInfo getPrmUserInfo(DataSource dataSource, long prmId){
        if(dataSource == null || prmId <= 0){
            throw new IllegalArgumentException();
        }
        PrmUserInfo prmUserInfo = new PrmUserInfo();
        List<Object> parameters = new ArrayList<>();
        parameters.add(prmId);
        try {
            List<Map<String, Object>> result = JDBCUtils.executeQuery(dataSource, "select * from " + Constant.PRM_USER_INFO + " where id=?", parameters);
            if(result == null || result.size() == 0){
                return null;
            }else{
                ReflectionUtil.cloneMapValueToBean(result.get(0), prmUserInfo);
                prmUserInfo.setDatas(result.get(0));
                return prmUserInfo;
            }
        } catch (SQLException e) {
            LOG.error("select prm user info by id failed, prmId:{}", prmId, e);
            throw new HappyQueryException(e);
        }
    }

    public static PrmUserInfo getPrmUserInfo(Connection connection, long prmId){
        if(connection == null || prmId <= 0){
            throw new IllegalArgumentException();
        }
        PrmUserInfo prmUserInfo = new PrmUserInfo();
        List<Object> parameters = new ArrayList<>();
        parameters.add(prmId);
        try {
            List<Map<String, Object>> result = JDBCUtils.executeQuery(connection, "select * from " + Constant.PRM_USER_INFO + " where id=?", parameters);
            if(result == null || result.size() == 0){
                return null;
            }else{
                ReflectionUtil.cloneMapValueToBean(result.get(0), prmUserInfo);
                prmUserInfo.setDatas(result.get(0));
                return prmUserInfo;
            }
        } catch (SQLException e) {
            LOG.error("select prm user info by id failed, prmId:{}", prmId, e);
            throw new HappyQueryException(e);
        }
    }

    public static PrmUserInfo getPrmUserInfoBySourceAndUserKey(DataSource dataSource, String userKey, String source){
        if(StringUtils.isNullOrEmpty(userKey) || StringUtils.isNullOrEmpty(source) || dataSource == null){
            throw new IllegalArgumentException();
        }
        PrmUserInfo prmUserInfo = new PrmUserInfo();
        List<Object> parameters = new ArrayList<>();
        parameters.add(userKey);
        parameters.add(source);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            List<Map<String, Object>> result = JDBCUtils.executeQuery(connection, "select * from " + Constant.PRM_USER_INFO + " where user_key=? and source=?", parameters);
            if(result == null || result.size() == 0){
                return null;
            }else{
                ReflectionUtil.cloneMapValueToBean(result.get(0), prmUserInfo);
                prmUserInfo.setDatas(result.get(0));
                return prmUserInfo;
            }
        } catch (SQLException e) {
            LOG.error("select prm user info by source and user key failed, userKey:{}, source:{}", userKey, source , e);
            throw new HappyQueryException(e);
        } finally {
            JDBCUtils.close(connection);
        }
    }

    public static int updatePrmUserInfo(Connection connection, Map<String, Object> datas, long prmId){
        try {
           return JDBCUtils.executeUpdateById(connection, Constant.PRM_USER_INFO, datas, "id", prmId);
        } catch (SQLException e) {
            throw new HappyQueryException("prmId is:" + prmId + " update prm user info by id failed", e);
        }
    }
}
