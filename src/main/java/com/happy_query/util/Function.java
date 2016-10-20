package com.happy_query.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * if you want to use template column to define functions, Please extend this class
 * Created by frio on 16/7/6.
 */
public class Function {
    static Logger LOG = LoggerFactory.getLogger(Function.class);
    Pattern FUNCTION_NAME_SPACE = Pattern.compile("([a-zA-Z0-9]+)\\((.*?)\\)");

////    public String renderExample(Row.Value r, DataDefinition d){
////        System.out.println("value is :" + r.getViewValue());
////        System.out.println("data_definition_id is:" + d.getId());
////        return "";
////    }
////
////    public Object reverseRenderExample(String value, DataDefinition d){
////        System.out.println("value is:" + value);
////        System.out.println("data_definition_id is:" + d.getId());
////        return 1L;
////    }
//
//    /**
//     * do rendering
//     * @param value
//     * @param id
//     *
//     * @return
//     */
//    public Object render(Row.Value value, Long id, String type) {
//        try {
//            if(null == value){
//                return "";
//            }
//            DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(id);
//            if(dataDefinition instanceof DataDefinitionCacheManager.NullDataDefinition){
//                LOG.error("we can't find id:[{}] definition", id);
//                return "";
//            }
//
//            if (dataDefinition.getTemplate() != null
//                    && !StringUtils.isNullOrEmpty(dataDefinition.getTemplate())
//                    && dataDefinition.getTemplate().split(";").length <= 2){
//                Matcher m = FUNCTION_NAME_SPACE.matcher(dataDefinition.getTemplate());
//                if(m.find()){
//                    String ls = m.group(2);
//                    String methodName = m.group(1);
//                    if(Arrays.asList(ls.split(",")).contains(type)){
//                        Class<?>[] paramCls = new Class[]{Row.Value.class, DataDefinition.class};
//                        Object[] args = new Object[]{value, dataDefinition};
//                        String viewValue = ReflectionUtil.invokeMethod(methodName, this, paramCls, args).toString();
////                        value.setViewValue(viewValue);
//                        return viewValue;
//                    }
//                }
//            }else{
//                return value.getValue();
//            }
//        } catch (Exception e){
//            LOG.error("unexpected exception, dId is [{}], argValue is [{}]", id, value.getValue(), e);
//        }
//        return value.getValue();
//    }
//
//    /**
//     * reverse render value
//     * @param value
//     * @param id
//     * @return
//     */
//    public Object reverseRender(String value, Long id, String type) {
//        try {
//            DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition(id);
//            if(dataDefinition instanceof DataDefinitionCacheManager.NullDataDefinition){
//                LOG.error("we can't find id:[{}] definition", id);
//                return "";
//            }
//            if (dataDefinition.getTemplate() != null
//                    && !StringUtils.isNullOrEmpty(dataDefinition.getTemplate())
//                    && dataDefinition.getTemplate().split(";").length == 2){
//                String s = dataDefinition.getTemplate().split(";")[1];
//                Matcher m = FUNCTION_NAME_SPACE.matcher(s);
//                if(m.find()) {
//                    String methodName = m.group(1);
//                    List<String> ls = Arrays.asList(m.group(2).split(","));
//                    if(ls.contains(type)){
//                        Class<?>[] paramCls = new Class[]{String.class, DataDefinition.class};
//                        Object[] args = new Object[]{value, dataDefinition};
//                        return ReflectionUtil.invokeMethod(methodName, this, paramCls, args);
//                    }
//                }
//            }
//            //we need to add convert logic here
//            if(dataDefinition.getDataTypeEnum() == DataDefinitionDataType.BOOLEAN
//                    || dataDefinition.getDataTypeEnum() == DataDefinitionDataType.INT
//                    || dataDefinition.getDataTypeEnum() == DataDefinitionDataType.DOUBLE
//                    || dataDefinition.getDataTypeEnum() == DataDefinitionDataType.FLOAT
//                    || dataDefinition.getDataTypeEnum() == DataDefinitionDataType.DATETIME){
//                if(StringUtils.isNullOrEmpty(value)){
//                    return 0;
//                }
//            }
//            return value;
//        } catch (Exception e){
//            LOG.error("unexpected exception, dId is: [{}], argValue:[{}]", id, value, e);
//        }
//        return value;
//    }
}
