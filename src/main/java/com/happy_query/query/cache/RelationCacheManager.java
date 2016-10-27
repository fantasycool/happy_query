package com.happy_query.query.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.happy_query.domain.KeyRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by frio on 16/10/21.
 */
public class RelationCacheManager {
    static Logger LOG = LoggerFactory.getLogger(RelationCacheManager.class);
    public static DataSource dataSource;
    public static LoadingCache<String, Set<String>> cache = CacheBuilder.newBuilder()
            .maximumSize(2000)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Set<String>>() {
                        public Set<String> load(String key) throws Exception {
                            return createRelationKeys(key);
                        }
                    });

    private static Set<String> createRelationKeys(String key) {
        return KeyRelation.getAffectedRelationKeys(key, dataSource);
    }

    public static Set<String> getValue(String k) throws ExecutionException {
        return cache.get(k);
    }
}
