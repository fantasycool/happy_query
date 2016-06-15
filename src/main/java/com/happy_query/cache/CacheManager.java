package com.happy_query.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.happy_query.parser.dao.DataDefinitionDao;
import com.happy_query.parser.definition.DataDefinition;

import javax.sql.DataSource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Cache Manager
 * max number:2000
 * expired after 10 minutes
 *
 * Created by frio on 16/6/15.
 */
public class CacheManager {
    private DataSource dataSource;

    public CacheManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private LoadingCache<Object, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(2000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<Object, Object>() {
                        public Object load(Object key) throws Exception {
                            return createValue(key);
                        }
                    });

    public Object getValue(Object k) throws ExecutionException {
        return cache.get(k);
    }

    public void putValue(Object k, Object v) throws ExecutionException {
        cache.put(k, v);
    }

    public Object createValue(Object key) {
        if (key instanceof DataDefinition) {
            if (key != null && ((DataDefinition) key).getId() > 0) {
                DataDefinitionDao.getDataDefinition(dataSource, ((DataDefinition) key).getId());
            }
        }
        return null;
    }

}
