package com.happy_query.query;

import com.happy_query.domain.DataDefinitionDataType;
import com.happy_query.query.cache.DataDefinitionCacheManager;
import com.happy_query.parser.IJsonSqlParser;
import com.happy_query.parser.JsonSqlParser;
import com.happy_query.parser.SQLQueryAssembly;
import com.happy_query.domain.DataDefinition;
import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.ReflectionUtil;
import com.happy_query.domain.DataDefinitionValue;
import com.mysql.jdbc.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

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
        LOG.info("HAPPY QUERY: query sql is:{}", querySql);
        LOG.info("HAPPY QUERY: count sql is:{}", countSql);
        List<Map<String, Object>> resultList = new ArrayList<>();

        Integer count = 0;
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            long t1 = System.currentTimeMillis();
            List<Map<String, Object>> datas = JDBCUtils.executeQuery(connection,
                    querySql, new ArrayList<>());
            LOG.info("HAPPY QUERY: query time is:{}", System.currentTimeMillis() - t1);
            for(Map<String, Object> m : datas){
                Long prmId = Long.valueOf(m.get("id").toString());
                Map<String, Object> keyDatas = new HashMap<>();
                dataAssemble(prmId, null, keyDatas, m, false, null);
                resultList.add(keyDatas);
            }
            t1 = System.currentTimeMillis();
            List<Map<String, Object>> countDatas = JDBCUtils.executeQuery(connection,
                    countSql, new ArrayList<>());
            LOG.info("HAPPY QUERY: count time is:{}", System.currentTimeMillis() - t1);
            //remove null datas
            removeNullDatasFromDataMap(resultList);
            count = countDatas != null && countDatas.size() > 0 ? Integer.valueOf(countDatas.get(0).get(COUNT_NUM).toString()) : 0;
        }catch(SQLException e){
            LOG.error("query met db exception, jsonQuery:{}, start:{}, size:{}", jsonQuery, start, size, e);
            throw new HappyQueryException("query met db exception", e);
        }finally {
            JDBCUtils.close(connection);
        }
        Pair<Integer, List<Map<String, Object>>> result = new Pair<>(count, resultList);
        return result;
    }

    /**
     * remove null datas
     * @param countDatas
     */
    private void removeNullDatasFromDataMap(List<Map<String, Object>> countDatas) {
        for(Map<String, Object> m : countDatas){
            for(Iterator<Map.Entry<String, Object>> it = m.entrySet().iterator(); it.hasNext(); ){
                Map.Entry<String, Object> entry = it.next();
                DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(entry.getKey());
                if(!(dataDefinition instanceof DataDefinitionCacheManager.NullDataDefinition)){
                    if(dataDefinition.getDataTypeEnum() == DataDefinitionDataType.STRING
                            && (entry.getValue() == null || StringUtils.isEmptyOrWhitespaceOnly(entry.getValue().toString()))){
                        m.remove(entry.getKey());
                    }else if(dataDefinition.getDataTypeEnum() == DataDefinitionDataType.BOOLEAN
                            || dataDefinition.getDataTypeEnum() == DataDefinitionDataType.INT
                            || dataDefinition.getDataTypeEnum() == DataDefinitionDataType.DOUBLE){
                        if(entry.getValue() == null){
                            continue;
                        }
                        BigDecimal bigDecimal = new BigDecimal(entry.getValue().toString());
                        if(bigDecimal.compareTo(new BigDecimal(-1)) == 0){
                            it.remove();
                        }
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Object> getPrmUserInfo(Long prmId, List<String> keys, Connection connection) {
        if(null == prmId || connection == null){
            throw new IllegalArgumentException();
        }
        List<Object> args = new ArrayList<>();
        args.add(prmId);
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> prmDatas = new HashMap<>();
        try {
            List<Map<String, Object>> list = JDBCUtils.executeQuery(connection, "select * from " + Constant.PRM_USER_INFO + " where id=?", args);
            removeNullDatasFromDataMap(list);
            dataAssemble(prmId, connection, prmDatas, list.get(0), true, keys);
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
     * @param keys
     */
    private void dataAssemble(Long prmId, Connection connection, Map<String, Object> prmDatas, Map<String, Object> data, Boolean lazyLoadDataDefinitionValues, List<String> keys) {

        for(Map.Entry<String, Object> entry : data.entrySet()){
            DataDefinition dd = DataDefinitionCacheManager.getDataDefinition(entry.getKey());
//            if(dd instanceof DataDefinitionCacheManager.NullDataDefinition){
//                continue;
//            }
            prmDatas.put(entry.getKey(), entry.getValue());
        }
        if(lazyLoadDataDefinitionValues){
            List<DataDefinitionValue> dataDefinitionValues = getDataDefinitionValues(prmId, connection, keys);
            for(DataDefinitionValue ddv : dataDefinitionValues){
                DataDefinition dd = DataDefinitionCacheManager.getDataDefinition(ddv.getDdRefId());
                String keyName = dd.getKey();
                try{
                    Object value = ddv.getNotNullValue();
                    prmDatas.put(keyName, value);
                }catch(HappyQueryException e){
                    LOG.error("dataAssemble failed", e);
                    prmDatas.put(keyName, null);
                }
            }
        }
    }

    private List<DataDefinitionValue> getDataDefinitionValues(Long prmId, Connection connection, List<String> keys) {
        if(null == prmId || connection == null || prmId <= 0){
            throw new IllegalArgumentException();
        }
        List<DataDefinitionValue> dataOptionValues = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        parameters.add(prmId);
        generateParameters(parameters, keys);
        try {
            List<Map<String, Object>> list = JDBCUtils.executeQuery(connection,
                    generateQuestionMark(keys), parameters);
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

    /**
     * 生成List questions mark对应的parameters
     * @param parameters
     * @param keys
     * @return
     */
    private void generateParameters(List<Object> parameters, List<String> keys) {
        if(keys != null && keys.size() > 0){
            for(String key : keys){
                DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(key);
                parameters.add(dataDefinition.getId());
            }
        }
    }

    /**
     * 生成Preparedstatement question mark
     * @param keys
     * @return
     */
    private String generateQuestionMark(List<String> keys) {
        if(keys == null || keys.size() == 0){
            return "select * from " + Constant.DATA_DEFINITION_VALUE + " where prm_id=? and status=0";
        }else{
            StringBuilder sb = new StringBuilder();
            sb.append("select * from " + Constant.DATA_DEFINITION_VALUE);
            sb.append(" where prm_id=? and status=0");
            sb.append(" and dd_ref_id in (" );
            for(int i = 0; i < keys.size(); i ++){
                sb.append("?");
                if(i < (keys.size() - 1) ){
                    sb.append(",");
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
