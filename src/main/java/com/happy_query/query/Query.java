package com.happy_query.query;

import com.happy_query.cache.DataDefinitionCacheManager;
import com.happy_query.parser.IJsonSqlParser;
import com.happy_query.parser.JsonSqlParser;
import com.happy_query.parser.SQLQueryAssembly;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.domain.QueryResult;
import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.ReflectionUtil;
import com.happy_query.writer.domain.DataDefinitionValue;
import com.mysql.jdbc.StringUtils;
import org.javatuples.Pair;
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
 * Query to get result
 * Created by frio on 16/6/15.
 */
public class Query implements IQuery {
    public static final String COUNT_NUM = "countNum";
    private DataSource dataSource;
    static Logger LOG = LoggerFactory.getLogger(Query.class);
    private IJsonSqlParser jsonSqlParser;


    public Query(DataSource dataSource) {
        this.dataSource = dataSource;
        jsonSqlParser = new JsonSqlParser();
    }

    public Query(){

    }

    @Override
    public Pair<Integer, List<Map<String, Object>>> queryPrmUserInfosByJson(String jsonQuery, int start, int size) {
        if(StringUtils.isEmptyOrWhitespaceOnly(jsonQuery)){
            throw new IllegalArgumentException();
        }
        Pair<String, List<String>> p = jsonSqlParser.convertJsonToQuerySql(jsonQuery);
        String querySql = SQLQueryAssembly.assemblyQuerySql(p, start, size, true);
        String countSql = SQLQueryAssembly.assemblyQuerySql(p, start, size, false);
        List<Map<String, Object>> resultList = new ArrayList<>();

        Integer count = 0;
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            List<Map<String, Object>> datas = JDBCUtils.executeQuery(connection,
                    querySql, new ArrayList<>());
            for(Map<String, Object> m : datas){
                Long prmId = Long.valueOf(m.get("id").toString());
                Map<String, Object> keyDatas = new HashMap<>();
                dataAssemble(prmId, null, keyDatas, m, false);
                resultList.add(keyDatas);
            }
            List<Map<String, Object>> countDatas = JDBCUtils.executeQuery(connection,
                    countSql, new ArrayList<>());
            count = Integer.valueOf(countDatas.get(0).get(COUNT_NUM).toString());
        }catch(SQLException e){
            LOG.error("query met db exception, jsonQuery:{}, start:{}, size:{}", jsonQuery, start, size, e);
            throw new HappyQueryException("query met db exception", e);
        }finally {
            JDBCUtils.close(connection);
        }
        Pair<Integer, List<Map<String, Object>>> result = new Pair<>(count, resultList);
        return result;
    }

    @Override
    public Map<String, Object> getPrmUserInfo(Long prmId, List<String> keys, Connection connection) {
        if(null == prmId || connection == null){
            throw new IllegalArgumentException();
        }
        connection = getConnection(connection);
        List<Object> args = new ArrayList<>();
        args.add(prmId);
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> prmDatas = new HashMap<>();
        try {
            List<Map<String, Object>> list = JDBCUtils.executeQuery(connection, "select * from " + Constant.PRM_USER_INFO + " where id=?", args);
            dataAssemble(prmId, connection, prmDatas, list.get(0), true);
        } catch (SQLException e) {
            LOG.error("getPrmUserInfo query failed, prmId:{}", prmId);
            throw new HappyQueryException(e);
        }
        if(keys != null && keys.size() > 0){
            for(String key : keys){
                result.put(key, prmDatas.get(key));
            }
            return result;
        }else{
            return prmDatas;
        }
    }

    /**
     * 对数据库查出的数据进行组合拼装
     * @param prmId
     * @param connection
     * @param prmDatas
     * @param data
     */
    private void dataAssemble(Long prmId, Connection connection, Map<String, Object> prmDatas, Map<String, Object> data, Boolean lazyLoadDataDefinitionValues) {

        for(Map.Entry<String, Object> entry : data.entrySet()){
            DataDefinition dd = DataDefinitionCacheManager.getDataDefinition(entry.getKey());
            if(dd instanceof DataDefinitionCacheManager.NullDataDefinition){
                continue;
            }
            prmDatas.put(entry.getKey(), entry.getValue());
        }
        if(lazyLoadDataDefinitionValues){
            List<DataDefinitionValue> dataDefinitionValues = getDataDefinitionValues(prmId, connection);
            for(DataDefinitionValue ddv : dataDefinitionValues){
                DataDefinition dd = DataDefinitionCacheManager.getDataDefinition(ddv.getDdRefId());
                String keyName = dd.getKey();
                try{
                    Object value = ddv.getNotNullValue();
                    prmDatas.put(keyName, value);
                }catch(HappyQueryException e){
                    prmDatas.put(keyName, null);
                }
            }
        }
    }

    private List<DataDefinitionValue> getDataDefinitionValues(Long prmId, Connection connection) {
        if(null == prmId || connection == null || prmId <= 0){
            throw new IllegalArgumentException();
        }
        connection = getConnection(connection);
        List<DataDefinitionValue> dataOptionValues = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        parameters.add(prmId);
        try {
            List<Map<String, Object>> list = JDBCUtils.executeQuery(connection,
                    "select * from " + Constant.DATA_DEFINITION_VALUE + " where prm_id=? and status=0", parameters);
            if(null == list || list.size() == 0){
                return dataOptionValues;
            }
            for(Map<String, Object> m : list){
                DataDefinitionValue dataDefinitionValue = new DataDefinitionValue();
                ReflectionUtil.cloneMapValueToBean(m, dataDefinitionValue);
                dataOptionValues.add(dataDefinitionValue);
            }
            return dataOptionValues;
        } catch (SQLException e) {
            LOG.error("execute query getDataOpertionValues failed, prmId:{}", prmId, e);
            throw new HappyQueryException(e);
        }
    }

    private Connection getConnection(Connection connection) {
        if(connection == null){
            try {
                connection = dataSource.getConnection();
            } catch (SQLException e) {
                LOG.error("get connection failed", e);
                throw new HappyQueryException(e);
            }
        }
        return connection;
    }

//    public QueryResult queryByJsonLogic(JsonParseDataParam jsonParseDataParam) {
//        if (!jsonParseDataParam.check()) {
//            throw new IllegalArgumentException("invalid jsonParseDataParam");
//        }
//
//        String querySql = jsonSqlParser.convertJsonLogicToQuerySql(jsonParseDataParam);
//        String countSql = jsonSqlParser.convertJsonLogicToCountSql(jsonParseDataParam);
//        System.out.println(querySql);
//        System.out.println(countSql);
//        Connection connection = null;
//        try {
//            connection = dataSource.getConnection();
//            JDBCUtils.execute(connection, "SET SESSION group_concat_max_len = 1000000", new ArrayList<Object>());
//            long t1 = System.currentTimeMillis();
//            List<Map<String, Row.Value>> originalQueryResult = JDBCUtils.executeQuery(connection, querySql, new ArrayList(0));
//            System.out.println(String.format("query time is:%d" ,System.currentTimeMillis() - t1));
//            List<Map<String, Row.Value>> countQueryResult = JDBCUtils.executeQuery(connection, countSql, new ArrayList(0));
//            System.out.println(String.format("all time is:%d" ,System.currentTimeMillis() - t1));
//            LOG.info("##############happy query executing time ############### time:[{}]", System.currentTimeMillis() - t1);
//            System.out.println(String.format("##############happy query executing time ############### time:[%d]", System.currentTimeMillis() - t1));
//            QueryResult queryResult = QueryResult.createFromOrinalData(jsonParseDataParam, originalQueryResult, countQueryResult);
//            return queryResult;
//        } catch (SQLException e) {
//            LOG.error("querySqlIs:[{}], countSqlIs:[{}]", querySql, countSql);
//            System.out.println(String.format("querySqlIs:%s, countSqlIs:%s", querySql, countSql));
//            throw new HappyQueryException("query sql exception", e);
//        } finally {
//            JDBCUtils.close(connection);
//        }
//        return null;
//    }
//
//    public QueryResult queryByLeftId(long leftId, String categoryType) {
//        long t1 = System.currentTimeMillis();
//        String rightTable = Constant.RIGHT_TABLE_MAP.get(categoryType);
//        String leftTable = Constant.LEFT_TABLE_MAP.get(categoryType);
//        String leftIdColumn = Constant.LEFT_ID_COLUMNS.get(categoryType);
//
//        JsonParseDataParam jsonParseDataParam = new JsonParseDataParam();
//        jsonParseDataParam.setLeftTableName(leftTable);
//        jsonParseDataParam.setRightTableName(rightTable);
//        jsonParseDataParam.setLeftPrimaryId(leftIdColumn);
//        jsonParseDataParam.setLimitStart(0);
//        jsonParseDataParam.setSize(1);
//        jsonParseDataParam.setLeftOperationStr(leftIdColumn + "=" + leftId);
//        jsonParseDataParam.setConnectType("left");
//
//        String querySql = jsonSqlParser.getFreemarkerSql(jsonParseDataParam, "bb.left_id=" + leftId, "query");
//        String countSql = jsonSqlParser.getFreemarkerSql(jsonParseDataParam, "bb.left_id=" + leftId, "count");
//        Connection connection = null;
//        try {
//            connection = dataSource.getConnection();
//            JDBCUtils.execute(connection, "SET SESSION group_concat_max_len = 1000000", new ArrayList<Object>());
//            List<Map<String, Row.Value>> queryResult = JDBCUtils.executeQuery(connection, querySql, new ArrayList(0));
//            List<Map<String, Row.Value>> countResult = JDBCUtils.executeQuery(connection, countSql, new ArrayList(0));
//            return QueryResult.createFromOrinalData(jsonParseDataParam, queryResult, countResult);
//        } catch (SQLException e) {
//            throw new HappyQueryException("query by leftId:" + leftId + "failed", e);
//        } finally {
//            LOG.info("queryByLeftId time is [{}]", System.currentTimeMillis() - t1);
//            JDBCUtils.close(connection);
//        }
//    }
}
