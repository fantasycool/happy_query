package com.happy_query.writer;

import com.alibaba.fastjson.JSON;
import com.happy_query.query.cache.DataDefinitionCacheManager;
import com.happy_query.query.cache.RelationCacheManager;
import com.happy_query.domain.DataDefinition;
import com.happy_query.query.Query;
import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.domain.DataDefinitionValue;
import com.happy_query.domain.DbArg;
import com.happy_query.domain.PrmUserInfo;
import com.happy_query.util.NullChecker;
import com.jkys.moye.MoyeComputeEngine;
import com.jkys.moye.MoyeComputeEngineImpl;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

/**
 * Created by frio on 16/6/17.
 */
public class Writer implements IWriter {
    private static Logger LOG = LoggerFactory.getLogger(Writer.class);
    private DataSource dataSource;

    public Writer(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public Writer(){

    }

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
        DbArg dbArg = DbArg.createFromArgs(keyDatas, source, userKey, empName);
        PrmUserInfo prmUserInfo = dbArg.prmUserInfo;
        long prmId = 0;
        Connection connection = null;
        try {

            connection = dataSource.getConnection();
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);
            prmId = JDBCUtils.insertToTable(connection, Constant.PRM_USER_INFO, prmUserInfo.getDatas());
            prmUserInfo.setId(prmId);
            commentDatasFilling(keyDatas, connection, prmUserInfo);
            //reset because keyDatas may be updated
            dbArg = DbArg.createFromArgs(keyDatas, source, userKey, empName);
            updateDataDefinitionValues(dbArg.dataDefinitionValues, prmId, empName, null);
            relationUpdate(keyDatas, empName, connection, prmUserInfo);
            connection.commit();
        } catch (Exception e) {
            JDBCUtils.rollback(connection);
            throw new HappyQueryException("data written to user info failed", e);
        }finally {
            JDBCUtils.close(connection);
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
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);
            PrmUserInfo prmUserInfo = PrmUserInfo.getPrmUserInfo(dataSource, prmId);
            if (null == prmUserInfo) {
                throw new HappyQueryException("prmId:" + prmId + ", prmUserInfo not exists");
            }
            commentDatasFilling(keyDatas, connection, prmUserInfo);
            DbArg dbArg = DbArg.createFromArgs(keyDatas, prmUserInfo, connection);
            if(prmUserInfo.getDatas() != null && prmUserInfo.getDatas().size() > 0){
                PrmUserInfo.updatePrmUserInfo(connection, prmUserInfo.getDatas(), prmUserInfo.getId());
            }
            updateDataDefinitionValues(dbArg.dataDefinitionValues, prmUserInfo.getId(), empName, null);
            relationUpdate(keyDatas, empName, connection, prmUserInfo);
        }catch(HappyQueryException e){
            throw e;
        }catch(Exception e){
            e.printStackTrace();
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

    @Override
    public void updateOrInsertRecord(Map<String, Object> keyDatas, String userKey, String source, String empName) throws HappyQueryException {
        NullChecker.checkNull(keyDatas, userKey, source, empName);
        try {
            PrmUserInfo prmUserInfo = PrmUserInfo.getPrmUserInfoBySourceAndUserKey(dataSource, userKey, source);
            if(prmUserInfo == null){
                insertRecord(keyDatas, source, userKey, empName);
            }else{
                updateRecord(keyDatas, prmUserInfo.getId(), empName);
            }
        }catch (HappyQueryException e){
            throw e;
        }catch(Exception e){
            LOG.error("updateOrInsertRecord failed", e);
        }
    }

    /**
     **
     * relation update
     * 场景描述:
     * 1. eg:BMI指标,年龄的指标是随着身高,体重,出生日期的指标的变化而变化的.所以,当身高体重的指标产生变化时,同样的BMI根据
     *  计算公式也应该重新计算生成.
     * 2. 标签指标也是根据多个指标的条件组合拼装而成.当标签涉及的这些指标有一个或者多个产生变化时,标签的本身的值应该经过重新计算才行
     *
     * @param keyDatas
     * @param empName
     * @param connection
     * @param prmUserInfo
     * @throws ExecutionException
     */
    private void relationUpdate(Map<String, Object> keyDatas, String empName, Connection connection, PrmUserInfo prmUserInfo) throws ExecutionException {
        Set<String> relatedKeys = new HashSet<>();
        for(String key : keyDatas.keySet()){
            relatedKeys.addAll(RelationCacheManager.getValue(key));
        }
        //add logic: 如果是系统标签或者组标签或者手动标签,我们不需要联动更新
        relatedKeys.removeIf(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(s);
                if(dataDefinition.getType() == 1){
                    if(dataDefinition.getTagType() == Constant.HAND_BIAO_QIAN
                            || dataDefinition.getTagType() == Constant.GROUP_BIAO_QIAN
                            || dataDefinition.getTagType() == Constant.XI_TONG_BIAO_QIAN){
                        return true;
                    }
                }
                return false;
            }
        });
        Map<String, Object> updatedDatas = new HashMap<>();
        if(relatedKeys.size() > 0){
            Query query = new Query(dataSource);
            Map<String, Object> userDatas = query.getPrmUserInfo(prmUserInfo.getId(), null, connection);
            for(String key :relatedKeys){
                DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(key);
                String expression = dataDefinition.getComputationRule();
                try{
                    Object value = moyeComputeEngine.execute(expression, userDatas);
                    //为了保持和数字类型为空一致,未打标签的值统一设置为-1
                    if(dataDefinition.getType() == Constant.TAG_TYPE && Integer.valueOf(value.toString()) == 0){
                        value = -1;
                    }
                    updatedDatas.put(dataDefinition.getKey(), value);
                }catch(Exception e){
                    LOG.error("compute new dd value failed,expression:{}, userDatas:{}", expression, JSON.toJSONString(userDatas), e);
                }
            }
        }
        updateRecord(updatedDatas, prmUserInfo, empName, connection);
    }

    private void updateRecord(Map<String, Object> keyDatas, PrmUserInfo prmUserInfo, String empName, Connection connection) throws HappyQueryException {
        try {
            if(connection == null){
                throw new HappyQueryException("connection must not be null");
            }
            DbArg dbArg = DbArg.createFromArgs(keyDatas, prmUserInfo, connection);
            updateDataDefinitionValues(dbArg.dataDefinitionValues, prmUserInfo.getId(), empName, null);
            if(prmUserInfo.getDatas() != null && prmUserInfo.getDatas().size() > 0){
                PrmUserInfo.updatePrmUserInfo(connection, dbArg.prmUserInfo.getDatas(), prmUserInfo.getId());
            }
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
                        Object sourceValue = keyDatas.get(dataDefinition.getKey());
                        Object commentValue = userInfoDatas.get(dataDefinition.getChildComment().getKey());
                        if ((sourceValue != null && commentValue != null && sourceValue.toString().equals(commentValue.toString()))//comment的值和原始的值都不为空的情况下, 取出的comment的值equals原始的值
                                || commentValue == null  //comment的值本身为空
                                ) {
                            keyDatas.put(dataDefinition.getChildComment().getKey(), sourceValue);
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
            DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(dataDefinitionValue.getDdRefId());
            if(dataDefinition instanceof DataDefinitionCacheManager.NullDataDefinition){
                LOG.info("could not find datadefinition, key:{}", dataDefinition.getKey());
                continue;
            }
            String valueColumn = dataDefinitionValue.getValueColumn();
            Object value = dataDefinitionValue.getValue(dataDefinition.getDataTypeEnum());
            StringBuilder execSql = new StringBuilder();
            execSql.append("insert into ").append(Constant.DATA_DEFINITION_VALUE).append("(prm_id,dd_ref_id,emp_name,").append(valueColumn).append(")")
                    .append("values").append("(?,?,?,?) on duplicate key update ").append(valueColumn).append("=?, emp_name=?, status=0");
            List<Object> parameters = new ArrayList<Object>();
            parameters.add(prmId);
            parameters.add(dataDefinitionValue.getDdRefId());
            parameters.add(empName);
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
                LOG.error("update data definition value failed", e);
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
