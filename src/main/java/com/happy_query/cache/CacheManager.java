package com.happy_query.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.happy_query.parser.dao.DataDefinitionDao;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.util.TemplateUtil;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.StringReader;
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
    public static DataSource dataSource;
    static Logger LOG = LoggerFactory.getLogger(CacheManager.class);


    private static LoadingCache<Object, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(2000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<Object, Object>() {
                        public Object load(Object key) throws Exception {
                            return createValue(key);
                        }
                    });


    public static Object getValue(Object k) throws ExecutionException {
        return cache.get(k);
    }

    public static void putValue(Object k, Object v) throws ExecutionException {
        cache.put(k, v);
    }

    public static Object createValue(Object key) {
        if (key instanceof DataDefinition) {
            if (key != null && ((DataDefinition) key).getId() > 0) {
                DataDefinitionDao.getDataDefinition(dataSource, ((DataDefinition) key).getId());
            }
        }else if(key instanceof String){
            if(((String) key).startsWith("template_")){
                try {
                    Template t = new Template("templateName",
                            new StringReader(((String) key).replace(TemplateUtil.TEMPLATE_PREFIX, "")), TemplateUtil.configuration);
                    return t;
                }catch(Exception e){
                    LOG.error("init template failed!", e);
                }
            }
        }
        return null;
    }
}
