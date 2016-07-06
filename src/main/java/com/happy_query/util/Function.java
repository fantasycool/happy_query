package com.happy_query.util;

import com.happy_query.cache.CacheManager;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.query.domain.Row;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * if you use template column to define functions, Please extend this class
 * Created by frio on 16/7/6.
 */
public class Function {
    static Logger LOG = LoggerFactory.getLogger(Function.class);

    public String renderExample(Row.Value r, DataDefinition d){
        System.out.println("value is :" + r.getViewValue());
        System.out.println("data_definition_id is:" + d.getId());
        return "";
    }

    public Object reverseRenderExample(String value, DataDefinition d){
        System.out.println("value is:" + value);
        System.out.println("data_definition_id is:" + d.getId());
        return 1L;
    }

    /**
     * do rendering
     * @param value
     * @param id
     * @return
     */
    public String render(Row.Value value, Long id) {
        try {
            if(null == value){
                return "";
            }
            DataDefinition dataDefinition = (DataDefinition) CacheManager.getValue(DataDefinition.createDataDefinitionById(id));
            if(dataDefinition instanceof CacheManager.NullValue){
                LOG.error("we can't find id:[{}] definition", id);
                return "";
            }
            if (dataDefinition.getTemplate() != null
                    && !StringUtils.isNullOrEmpty(dataDefinition.getTemplate())
                    && dataDefinition.getTemplate().split(";").length <= 2){
                String methodName = dataDefinition.getTemplate().split(";")[0];
                Class<?>[] paramCls = new Class[]{Row.Value.class, DataDefinition.class};
                Object[] args = new Object[]{value, dataDefinition};
                String viewValue = ReflectionUtil.invokeMethod(methodName, this, paramCls, args).toString();
                value.setViewValue(viewValue);
                return viewValue;
            }else{
                value.setViewValue(value.toString());
                return value.getValue().toString();
            }
        } catch (ExecutionException e) {
            LOG.error("get cache value failed!", e);
            return value.toString();
        }
    }

    /**
     * reverse render value
     * @param value
     * @param id
     * @return
     */
    public Object reverseRender(String value, Long id) {
        try {
            DataDefinition dataDefinition = (DataDefinition) CacheManager.getValue(DataDefinition.createDataDefinitionById(id));
            if(dataDefinition instanceof CacheManager.NullValue){
                LOG.error("we can't find id:[{}] definition", id);
                return "";
            }
            if (dataDefinition.getTemplate() != null
                    && !StringUtils.isNullOrEmpty(dataDefinition.getTemplate())
                    && dataDefinition.getTemplate().split(";").length == 2){
                String methodName = dataDefinition.getTemplate().split(";")[1];
                Class<?>[] paramCls = new Class[]{String.class, DataDefinition.class};
                Object[] args = new Object[]{value, dataDefinition};
                return ReflectionUtil.invokeMethod(methodName, this, paramCls, args);
            }
            return value;
        } catch (ExecutionException e) {
            LOG.error("get cache value failed!", e);
            return value;
        }
    }
}
