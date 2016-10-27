package com.happy_query.query.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.happy_query.domain.DataOption;
import com.happy_query.parser.dao.DataDefinitionDao;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.DefinitionType;
import com.happy_query.util.HappyQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;
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

    public static Object getValue(Object k) throws ExecutionException {
        return cache.get(k);
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

    public static Object createDataDefinition(Object key) {
        if (key instanceof Long) {
            DataDefinition dataDefinition = DataDefinitionDao.getDataDefinition(dataSource, (long) key);
            if (null == dataDefinition) {
                return new NullDataDefinition();
            }
            fillChildComment(dataDefinition);
            fillOptions(dataDefinition);
            return dataDefinition;
        } else if (key instanceof String) {
            DataDefinition dataDefinition = DataDefinitionDao.getDataDefinitionByName(dataSource, key.toString());
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
