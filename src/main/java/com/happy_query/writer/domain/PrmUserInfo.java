package com.happy_query.writer.domain;

import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.ReflectionUtil;
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
}