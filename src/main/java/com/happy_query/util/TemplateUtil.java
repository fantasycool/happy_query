package com.happy_query.util;

import com.happy_query.cache.CacheManager;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Map;

/**
 * render value with str template
 * Created by frio on 16/6/16.
 */
public class TemplateUtil {
    public static Configuration configuration = new Configuration();
    static Logger LOG = LoggerFactory.getLogger(TemplateUtil.class);
    static{
        configuration.setObjectWrapper(new DefaultObjectWrapper());
    }
    public static String TEMPLATE_PREFIX = "template_";

    public static String getViewValueByTemplateStr(String templateStr,
                                                   Map<String, Object> contextParameters, CacheManager cacheManager) {
        try {
            Template t = (Template) cacheManager.getValue(TEMPLATE_PREFIX + templateStr);
            StringWriter out = new StringWriter();
            t.process(contextParameters, out);
            return out.toString();
        }catch(Exception e){
            LOG.error("getViewValueFailed, templateStr:[{}] t:[{}]",templateStr, e);
            return null;
        }
    }
}
