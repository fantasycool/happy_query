package com.happy_query.writer;

import com.alibaba.fastjson.JSON;
import com.happy_query.cache.DataDefinitionCacheManager;
import com.happy_query.cache.RelationCacheManager;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.query.Query;
import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.ReflectionUtil;
import com.happy_query.writer.domain.DataDefinitionValue;
import com.happy_query.writer.domain.DbArg;
import com.happy_query.writer.domain.PrmUserInfo;
import com.jkys.moye.MoyeComputeEngine;
import com.jkys.moye.MoyeComputeEngineImpl;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by frio on 16/6/17.
 */
public class Writer implements IWriter {
    private static Logger LOG = LoggerFactory.getLogger(Writer.class);
    private DataSource dataSource;
    /**
     * 计算引擎,传入参数
     */
    private MoyeComputeEngine moyeComputeEngine = new MoyeComputeEngineImpl();

    @Override
    public long insertRecord(Map<String, Object> keyDatas, String source, String userKey, String empName) {
        if(StringUtils.isNullOrEmpty(source) || StringUtils.isNullOrEmpty(userKey) || keyDatas == null){
            throw new IllegalArgumentException("illegal argument");
        }
        /**
         * 注意对于备注的双写集成在DbArg.createFromArgs方法中
         */
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
            /**
             * 要更新的指标是否包含具有备注并且为可筛选的指标,如果有则进行同步更新
             */
            commentDatasFilling(keyDatas, connection, prmUserInfo);
            DbArg dbArg = DbArg.createFromArgs(keyDatas, prmUserInfo, connection);
            updateDataDefinitionValues(dbArg.dataDefinitionValues, prmUserInfo.getId(), empName, null);
            /**
             * relation update
             * 场景描述:
             * 1. eg:BMI指标,年龄的指标是随着身高,体重,出生日期的指标的变化而变化的.所以,当身高体重的指标产生变化时,同样的BMI根据
             *  计算公式也应该重新计算生成.
             * 2. 标签指标也是根据多个指标的条件组合拼装而成.当标签涉及的这些指标有一个或者多个产生变化时,标签的本身的值应该经过重新计算才行
             */
            Set<String> relatedKeys = new HashSet<>();
            for(String key : keyDatas.keySet()){
                relatedKeys.addAll(RelationCacheManager.getValue(key));
            }
            Map<String, Object> updatedDatas = new HashMap<>();
            if(relatedKeys.size() > 0){
                Query query = new Query(dataSource);
                Map<String, Object> userInfoDatas = query.getPrmUserInfo(prmUserInfo.getId(), null, connection);
                for(String key :relatedKeys){
                    DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(key);
                    String expression = dataDefinition.getComputationRule();
                    Object value = moyeComputeEngine.execute(expression, userInfoDatas);
                    updatedDatas.put(dataDefinition.getKey(), value);
                }
            }
            updateRecord(updatedDatas, prmUserInfo, empName, connection);
        }catch(HappyQueryException e){
            throw e;
        }catch(Exception e){
            LOG.error("keyDatas:{}, prmId:{}, empName:{}", JSON.toJSONString(keyDatas), prmId, empName, e);
            JDBCUtils.rollback(connection);
            throw new HappyQueryException(String.format("keyDatas:%s, prmId:%d, empName:%s", JSON.toJSONString(keyDatas), prmId, empName));
        }finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
            }
            JDBCUtils.close(connection);
        }
    }

    private void updateRecord(Map<String, Object> keyDatas, PrmUserInfo prmUserInfo, String empName, Connection connection) throws HappyQueryException {
        try {
            if(connection == null){
                throw new HappyQueryException("connection must not be null");
            }
            DbArg dbArg = DbArg.createFromArgs(keyDatas, prmUserInfo, connection);
            updateDataDefinitionValues(dbArg.dataDefinitionValues, prmUserInfo.getId(), empName, null);
            PrmUserInfo.updatePrmUserInfo(connection, dbArg.prmUserInfo.getDatas(), prmUserInfo.getId());
        }catch(Exception e){
            throw new HappyQueryException(e);
        }
    }


    /**
     * 对于具有备注的指标进行填充数据的功能
     * @param keyDatas
     * @param connection
     * @param prmUserInfo
     */
    private void commentDatasFilling(Map<String, Object> keyDatas, Connection connection, PrmUserInfo prmUserInfo) {
        List<String> keys = getAllCommentKeys(keyDatas);
        if(keys != null && keys.size() > 0){
            Query query = new Query(dataSource);
            Map<String, Object> userInfoDatas = query.getPrmUserInfo(prmUserInfo.getId(), keys, connection);
            for(String key : userInfoDatas.keySet()){
                DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(key);
                //能够筛选的指标才需要进行双写原始指标和备注的指标
                if(dataDefinition.getQuery()) {
                    if (dataDefinition.getChildComment() != null && !(dataDefinition.getChildComment() instanceof DataDefinitionCacheManager.NullDataDefinition)) {
                        Object sourceValue = userInfoDatas.get(dataDefinition.getKey());
                        Object commentValue = userInfoDatas.get(dataDefinition.getChildComment().getKey());
                        if ((sourceValue != null && commentValue != null && sourceValue.toString().equals(commentValue.toString()))//comment的值和原始的值都不为空的情况下, 取出的comment的值equals原始的值
                                || commentValue == null  //comment的值本身为空
                                ) {
                            keyDatas.put(dataDefinition.getChildComment().getKey(), userInfoDatas.get(dataDefinition.getChildComment().getKey()));
                        }
                    }
                }
            }
        }
    }

    /**
     * 返回所有comment key和原始key
     * @param keyDatas
     * @return
     */
    private List<String> getAllCommentKeys(Map<String, Object> keyDatas) {
        List<String> result = new ArrayList<>();
        for(String key : keyDatas.keySet()){
            DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(key);
            if(dataDefinition.getChildComment() != null
                    && !(dataDefinition.getChildComment() instanceof DataDefinitionCacheManager.NullDataDefinition)){
                result.add(key);
                result.add(dataDefinition.getChildComment().getKey());
            }
        }
        return result;
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
