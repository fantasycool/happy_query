package com.happy_query.writer.domain;

import com.happy_query.cache.DataDefinitionCacheManager;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.query.Query;
import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by frio on 16/10/18.
 */
public class DbArg {
    static Logger LOG = LoggerFactory.getLogger(DbArg.class);
    public PrmUserInfo prmUserInfo;
    public List<DataDefinitionValue> dataDefinitionValues;

    private DbArg(){

    }

    /**
     * 新增一个用户调用产生
     * @param args
     * @param source
     * @param userKey
     * @return
     */
    public static DbArg createFromArgs(Map<String, Object> args, String source, String userKey){
        if(null == args || StringUtils.isBlank(source) || StringUtils.isBlank(userKey)){
            throw new IllegalArgumentException();
        }
        DbArg dbArg = new DbArg();
        dbArg.prmUserInfo = new PrmUserInfo();
        dbArg.prmUserInfo.setSource(source);
        dbArg.prmUserInfo.setUserKey(userKey);
        List<DataDefinition> prmDDs = new ArrayList<>();
        Map<String, Object> prmDatas = new HashMap<>();
        dbArg.prmUserInfo.setDds(prmDDs);
        dbArg.prmUserInfo.setDatas(prmDatas);

        List<DataDefinitionValue> dataDefinitionValues = new ArrayList<>();
        dbArg.dataDefinitionValues = dataDefinitionValues;
        assembleDataArgs(args, prmDDs, prmDatas, dataDefinitionValues);
        return dbArg;
    }



    /**
     * 对用户已有数据进行更新
     * @param args
     * @param prmUserInfo
     * @param connection
     *  we use this connection to permit data consist
     * @return
     */
    public static DbArg createFromArgs(Map<String, Object> args, PrmUserInfo prmUserInfo, Connection connection){
        if(null == args || prmUserInfo == null){
            throw new IllegalArgumentException();
        }
        Map<String, Object> prmDatas = new HashMap<>();
        /**
         * 备注指标是否自动同步更新依赖下面的逻辑:
         * 1: args中有筛选指标
         * 2: 筛选指标有备注指标
         *  2.1: 备注指标为空, 直接更新
         *  2.2: 备注指标不为空, 上次更新为系统自动更新
         * 查询args中筛选并且有备注的指标
         */
        List<String> keysHaveCommentAndCanBeQuery = getKeysHaveCommentAndCanBeQuery(args);
        Query query = new Query();
        Map<String, Object> datas = query.getPrmUserInfo(prmUserInfo.getId(), null, connection);
        for(String key : keysHaveCommentAndCanBeQuery){
            if(datas.get(key + Constant.COMMENT_PREFIX) == null && args.get(key) != null){
                prmDatas.put(key + Constant.COMMENT_PREFIX, args.get(key));
            }else if(args.get(key) != null && datas.get(key + Constant.COMMENT_PREFIX) != null
                    && args.get(key).toString().equals(datas.get(key+ Constant.COMMENT_PREFIX).toString())){
                prmDatas.put(key + Constant.COMMENT_PREFIX, args.get(key));
            }
        }
        DbArg dbArg = new DbArg();
        dbArg.prmUserInfo = prmUserInfo;
        List<DataDefinition> prmDDs = new ArrayList<>();

        dbArg.prmUserInfo.setDds(prmDDs);
        dbArg.prmUserInfo.setDatas(prmDatas);

        List<DataDefinitionValue> dataDefinitionValues = new ArrayList<>();
        dbArg.dataDefinitionValues = dataDefinitionValues;
        assembleDataArgs(args, prmDDs, prmDatas, dataDefinitionValues);
        return dbArg;
    }

    /**
     * 找出筛选并且有备注的指标
     * @param args
     * @return
     */
    public static List<String> getKeysHaveCommentAndCanBeQuery(Map<String, Object> args){
        List<String> result = new ArrayList<>();
        for(String key : args.keySet()){
            DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(key);
            if(dataDefinition.getQuery() && dataDefinition.getChildComment() != null){
                result.add(key);
            }
        }
        return result;
    }

    private static void assembleDataArgs(Map<String, Object> args, List<DataDefinition> prmDDs, Map<String, Object> prmDatas, List<DataDefinitionValue> dataDefinitionValues) {
        for(Map.Entry<String, Object> entry : args.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();

            DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(key);
            if(dataDefinition instanceof DataDefinitionCacheManager.NullDataDefinition){
                LOG.error("key {} does not exists", key);
                throw new HappyQueryException("key:" + key + "not exists");
            }

            if(dataDefinition.getLeftData()){
                prmDDs.add(dataDefinition);
                prmDatas.put(dataDefinition.getLefColName(), value);
            }else{
                DataDefinitionValue dataDefinitionValue = new DataDefinitionValue();
                dataDefinitionValue.setDdRefId(dataDefinition.getId());
                switch (dataDefinition.getDataTypeEnum()){
                    case BOOLEAN:
                        dataDefinitionValue.setIntValue(Long.valueOf(value.toString()));
                        break;
                    case INT:
                        dataDefinitionValue.setIntValue(Long.valueOf(value.toString()));
                        break;
                    case STRING:
                        dataDefinitionValue.setStrValue(value.toString());
                        break;
                    case TEXT:
                        dataDefinitionValue.setFeature(value.toString());
                        break;
                    case DATETIME:
                        if(value instanceof Long){
                            dataDefinitionValue.setIntValue(Long.valueOf(value.toString()));
                        }else if(value instanceof String){
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                Date date = simpleDateFormat.parse(value.toString());
                                dataDefinitionValue.setIntValue(date.getTime());
                            } catch (ParseException e) {
                                throw new HappyQueryException("key:" + dataDefinition.getKey() + "is not a correct datetime type, value is:" + value.toString());
                            }
                        }else{
                            throw new HappyQueryException("key:" + dataDefinition.getKey() + "is not a correct datetime type, value is:" + value.toString());
                        }
                        break;
                    case DOUBLE:
                        dataDefinitionValue.setDoubleValue(Double.valueOf(value.toString()));
                        break;
                    case FLOAT:
                        dataDefinitionValue.setDoubleValue(Double.valueOf(value.toString()));
                        break;
                }
                dataDefinitionValues.add(dataDefinitionValue);
            }
        }
    }

}