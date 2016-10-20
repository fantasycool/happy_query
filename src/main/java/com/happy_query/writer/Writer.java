package com.happy_query.writer;

import com.alibaba.fastjson.JSON;
import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.ReflectionUtil;
import com.happy_query.writer.domain.DataDefinitionValue;
import com.happy_query.writer.domain.DbArg;
import com.happy_query.writer.domain.PrmUserInfo;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * writer to import data
 * Created by frio on 16/6/17.
 */
public class Writer implements IWriter {
    private static Logger LOG = LoggerFactory.getLogger(Writer.class);
    private DataSource dataSource;

    @Override
    public long insertRecord(Map<String, Object> keyDatas, String source, String userKey, String empName) {
        if(StringUtils.isNullOrEmpty(source) || StringUtils.isNullOrEmpty(userKey) || keyDatas == null){
            throw new IllegalArgumentException("illegal argument");
        }
        DbArg dbArg = DbArg.createFromArgs(keyDatas, source, userKey);
        PrmUserInfo prmUserInfo = dbArg.prmUserInfo;
        Map<String, Object> prmUserInfoMap = ReflectionUtil.cloneBeanToMap(prmUserInfo);
        long prmId = 0;
        try {
            prmId = JDBCUtils.insertToTable(dataSource, Constant.PRM_USER_INFO, prmUserInfoMap);
            updateDataDefinitionValues(dbArg.dataDefinitionValues, prmId, empName, null);
        } catch (SQLException e) {
            throw new HappyQueryException("data written to user info failed", e);
        }
        return prmId;
    }

    @Override
    public void updateRecord(Map<String, Object> keyDatas, long prmId, String empName) throws HappyQueryException {
        if(StringUtils.isNullOrEmpty(empName) || keyDatas == null){
            throw new IllegalArgumentException("illegal argument");
        }
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            PrmUserInfo prmUserInfo = PrmUserInfo.getPrmUserInfo(dataSource, prmId);
            if (null == prmUserInfo) {
                throw new HappyQueryException("prmId:" + prmId + ", prmUserInfo not exists");
            }
            DbArg dbArg = DbArg.createFromArgs(keyDatas, prmUserInfo, connection);
            updateDataDefinitionValues(dbArg.dataDefinitionValues, prmUserInfo.getId(), empName, null);
        }catch(HappyQueryException e){
            throw e;
        }catch(Exception e){
            LOG.error("keyDatas:{}, prmId:{}, empName:{}", JSON.toJSONString(keyDatas), prmId, empName, e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                LOG.error("rollback error", e);
            }
            throw new HappyQueryException(String.format("keyDatas:%s, prmId:%d, empName:%s", JSON.toJSONString(keyDatas), prmId, empName));
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("close error", e);
            }
        }
    }

    /**
     * 更新纵向表的指标的数据
     * @param dataDefinitionValues
     * @param prmId
     * @throws SQLException
     */
    private void updateDataDefinitionValues(List<DataDefinitionValue> dataDefinitionValues, long prmId, String empName, Connection connection) throws SQLException {
        Map<String, List<List<Object>>> cachPrSqls = new HashMap<String, List<List<Object>>>();
        for(DataDefinitionValue dataDefinitionValue : dataDefinitionValues){
            String valueColumn = dataDefinitionValue.getValueColumn();
            Object value = dataDefinitionValue.getNotNullValue();
            StringBuilder execSql = new StringBuilder();
            execSql.append("insert into ").append(Constant.DATA_DEFINITION_VALUE).append("(prm_id,dd_ref_id").append(valueColumn).append(")")
                    .append("values").append("(?,?,?,?) on duplicate key update ").append(valueColumn).append("=?, emp_name=?, status=0");
            List<Object> parameters = new ArrayList<Object>();
            parameters.add(prmId);
            parameters.add(dataDefinitionValue.getDdRefId());
            parameters.add(value);
            parameters.add(value);
            parameters.add(empName);
            if (cachPrSqls.get(execSql.toString()) != null) {
                cachPrSqls.get(execSql.toString()).add(parameters);
            } else {
                List<List<Object>> list = new ArrayList<List<Object>>();
                list.add(parameters);
                cachPrSqls.put(execSql.toString(), list);
            }
        }
        if(connection == null){
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            try {
                for (String sql : cachPrSqls.keySet()) {
                    JDBCUtils.batchExecuteUpdate(connection, sql, cachPrSqls.get(sql));
                }
                connection.commit();
            }catch(Exception e){
                connection.rollback();
                throw new HappyQueryException("update data in data_definition_value failed", e);
            }finally {
                connection.close();
            }
        }else{
            for (String sql : cachPrSqls.keySet()) {
                JDBCUtils.batchExecuteUpdate(connection, sql, cachPrSqls.get(sql));
            }
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
