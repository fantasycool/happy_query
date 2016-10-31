package com.happy_query.query.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.happy_query.domain.DataDefinition;
import com.happy_query.domain.DataOption;
import com.happy_query.domain.DefinitionType;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.jkys.moye.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Cache Manager
 * max number:2000
 * expired after 10 minutes
 * Created by frio on 16/6/15.
 */
public class DataDefinitionCacheManager {
    static Logger LOG = LoggerFactory.getLogger(DataDefinitionCacheManager.class);
    public static DataSource dataSource;
    public static LoadingCache<Object, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(2000)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<Object, Object>() {
                        public Object load(Object key) throws Exception {
                            return createDataDefinition(key);
                        }
                    });

    public DataDefinitionCacheManager(DataSource _dataSource){
        dataSource = _dataSource;
    }

    public static Object getValue(Object k) throws ExecutionException {
        return cache.get(k);
    }

    /**
     * 初始化所有字典数据
     */
    public static void init(){
        try {
            List<Map<String, Object>> list = JDBCUtils.executeQuery(dataSource, "select key from " + DataDefinition.TABLE_NAME + " where status=0", new ArrayList<>());
            for(Map<String, Object> m: list){
                String key = m.get("key").toString();
                getDataDefinition(key);
            }
        } catch (SQLException e) {
            throw new HappyQueryException("dds init failed!", e);
        }
    }

    /**
     * 清除掉指标key的缓存
     * @param key
     */
    public static void delByKey(String key){
        cache.invalidate(key);
    }

    /**
     * 根据id或者key读取指标
     * @param k
     * @return
     */
    public static DataDefinition getDataDefinition(Object k){
        try {
            Object object = cache.get(k);
            if(!(object instanceof DataDefinition)){
                throw new HappyQueryException("return object is not DD type");
            }
            return (DataDefinition)object;
        } catch (ExecutionException e) {
            LOG.error("DataDefinitionCacheManager get dd error");
            throw new HappyQueryException("DataDefinitionCacheManager get dd error, arg:" + k.toString());
        }
    }

    /**
     * 根据打上标签的标签列表,返回有哪些组标签触发
     * @param keys 打上标签的标签列表
     * @return
     */
    public static List<String> groupDdTriggered(List<String> keys){
        MoyeComputeEngine moyeComputeEngine = new MoyeComputeEngineImpl();
        List<DataDefinition> groupDataDefinitions = DataDefinition.queryGroupDataDefinitions(dataSource);
        List<String> resultGroupKeys = new ArrayList<>();
        for(DataDefinition dataDefinition :groupDataDefinitions){
            String computationRule = dataDefinition.getComputationRule();
            if(StringUtils.isBlank(computationRule)){
                continue;
            }
            Map<String, Object> context = new HashMap<>();
            List<String> relationKeys = getKeysFromComputationRule(dataDefinition.getComputationRule());
            for(String key : relationKeys){
                context.put(key, 0);
            }
            for(String key : keys){
                context.put(key, 1);
            }
            if(validateAllZeroValues(context)){
                continue;
            }
            Object value = moyeComputeEngine.execute(dataDefinition.getComputationRule(), context);
            if(value != null && value.toString().equals("0")){
                resultGroupKeys.add(dataDefinition.getKey());
            }
        }
        return resultGroupKeys;
    }

    /**
     * 根據Lisp computation rule 获取relation keys
     * @return
     */
    public static List<String> getKeysFromComputationRule(String computationRule){
        MoyeParser moyeParser = new MoyeParserImpl();
        List<Word> words = moyeParser.parseExpression(computationRule);
        List<String> result = new ArrayList<>();
        for(Word w : words){
            if(w instanceof DynamicVariable){
                result.add(w.getName());
            }
        }
        return result;
    }

    private static boolean validateAllZeroValues(Map<String, Object> context) {
        for(Object key : context.keySet()){
            if(context.get(key).toString().equals("1")){
                return false;
            }
        }
        return true;
    }




    public static Object createDataDefinition(Object key) {
        if (key instanceof Long) {
            DataDefinition dataDefinition = DataDefinition.getDataDefinition(dataSource, (long) key);
            if (null == dataDefinition) {
                return new NullDataDefinition();
            }
            fillChildComment(dataDefinition);
            fillOptions(dataDefinition);
            return dataDefinition;
        } else if (key instanceof String) {
            DataDefinition dataDefinition = DataDefinition.getDataDefinitionByName(dataSource, key.toString());
            if (null == dataDefinition) {
                return new NullDataDefinition();
            }
            dataDefinition.initEnum();
            fillChildComment(dataDefinition);
            return dataDefinition;
        }
        return new NullDataDefinition();
    }

    /**
     * 填充DataOption选项
     * @param dataDefinition
     */
    private static void fillOptions(DataDefinition dataDefinition) {
        if(dataDefinition.getDefinitionTypeEnum() == DefinitionType.SELECT
                || dataDefinition.getDefinitionTypeEnum() == DefinitionType.MULTISELECT){
            List<DataOption> dataOptionList = DataOption.queryDataOptionsByDDId(dataSource, dataDefinition.getId());
            dataDefinition.setDataOptionList(dataOptionList);
        }
    }

    private static void fillChildComment(DataDefinition dataDefinition) {
        dataDefinition.initEnum();
        if(dataDefinition.getParentId() != null){
            DataDefinition parent = getDataDefinition(dataDefinition.getId());
            dataDefinition.setParent(parent);
        }
        if(dataDefinition.getChildCommentName() != null){
            DataDefinition childComment = getDataDefinition(dataDefinition.getChildCommentName());
            dataDefinition.setChildComment(childComment);
        }
    }

    public static class NullDataDefinition extends DataDefinition {
        //表示空指,没有取到对应的指标
    }
}
