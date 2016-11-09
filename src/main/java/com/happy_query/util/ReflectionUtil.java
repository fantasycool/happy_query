package com.happy_query.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by frio on 16/6/29.
 */
public class ReflectionUtil {
    private static Logger LOG = LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * 獲取Class的私有屬性
     *
     * @param theClass
     * @return
     */
    public static List<Field> getPrivateFields(Class<?> theClass) {
        List<Field> privateFields = new ArrayList<>();

        Field[] fields = theClass.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isPrivate(field.getModifiers())) {
                privateFields.add(field);
            }
        }
        return privateFields;
    }

    /**
     * @param methodName
     * @param function
     * @param paramCls
     * @param args
     * @return
     */
    public static Object invokeMethod(String methodName, Function function, Class<?>[] paramCls, Object[] args) {

        return null;
    }

    /**
     * 将Bean的属性转化为Map
     *
     * @param bean
     * @return
     */
    public static Map<String, Object> cloneBeanToMap(Object bean) {
        if (bean == null) {
            throw new IllegalArgumentException("arg bean is null");
        }
        Map<String, Object> result = new HashMap<>();
        for (Field field : bean.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.getType().equals(Date.class)) {
                    Date date = (Date) field.get(bean);
                    if(date != null){
                        result.put(getColumnNameByField(field.getName()), date.getTime());
                    }
                }else if(field.getType().equals(Boolean.class)) {
                    if (field.get(bean) != null && Boolean.valueOf(field.get(bean).toString())) {
                        result.put(getColumnNameByField(field.getName()), 0);
                    }
                }else if(field.getType().equals(String.class)){
                    if (field.get(bean)!=null){
                        result.put(getColumnNameByField(field.getName()), field.get(bean).toString());
                    }
                } else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                    result.put(getColumnNameByField(field.getName()), field.get(bean));
                } else if (field.getType().equals(Integer.class)) {
                    result.put(getColumnNameByField(field.getName()), field.get(bean));
                } else if ((field.getType().equals(Double.class) || field.getType().equals(double.class))) {
                    result.put(getColumnNameByField(field.getName()), field.get(bean));
                } else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
                    result.put(getColumnNameByField(field.getName()), field.get(bean));
                } else {
                    continue;
                }
            } catch (Exception e) {
                LOG.error("clone bean to Map failed", e);
            }
        }
        for(Iterator<Map.Entry<String, Object>> it = result.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, Object> entry = it.next();
            if(entry.getValue() == null){
                it.remove();
            }
        }
        return result;
    }

    /**
     * 拷贝m的属性到Bean的属性
     *
     * @param m
     * @param bean
     */
    public static void cloneMapValueToBean(Map<String, Object> m, Object bean) {
        if (m == null) {
            throw new IllegalArgumentException();
        }
        List<Field> fields = getPrivateFields(bean.getClass());
        Map<String, Field> mapper = new HashMap<>();
        for (Field f : fields) {
            mapper.put(f.getName(), f);
        }
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            String key = ReflectionUtil.getFieldNameByColumnName(entry.getKey());
            Object value = entry.getValue();
            if (mapper.get(key) == null || value == null) {
                continue;
            }
            Field field = mapper.get(key);
            try {
                field.setAccessible(true);
                if (field.getType().equals(Date.class) && (value instanceof Long)) {
                    field.set(bean, new Date((long) value));
                } else if(field.getType().equals(Boolean.class)){
                    int v = Integer.valueOf(value.toString());
                    if(v == 1){
                        field.set(bean, true);
                    }else{
                        field.set(bean, false);
                    }
                } else if (field.getType().equals(Date.class) && (value instanceof String)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date d = sf.parse(value.toString());
                    field.set(bean, d);
                } else if (field.getType().equals(String.class) && (value instanceof String)){
                    field.set(bean, value);
                } else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                    field.set(bean, Long.valueOf(value.toString()));
                } else if (field.getType().equals(Integer.class)) {
                    field.set(bean, Integer.valueOf(value.toString()));
                } else if ((field.getType().equals(Double.class) || field.getType().equals(double.class))) {
                    field.set(bean, Double.valueOf(value.toString()));
                } else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
                    field.set(bean, Float.valueOf(value.toString()));
                } else {
                    continue;
                }
            } catch (IllegalAccessException e) {
                LOG.error("set value, fieldName:[{}]", e, field.getName());
                throw new HappyQueryException("cloneMapToBean failed", e);
            } catch (ParseException e) {
                LOG.error("data parse failed, fieldName:[{}]", e, field.getName());
                throw new HappyQueryException("cloneMapToBean failed", e);
            } catch (Exception e) {
                LOG.error("data type convert cloneMapValueToBean failed, field name is:[{}]", e, field.getName());
                throw new HappyQueryException("cloneMapToBean failed", e);
            }
        }
    }

    /**
     * convert like this:
     * eg:userId -> user_id
     *
     * @param fieldName
     * @return
     */
    public static String getColumnNameByField(String fieldName) {
        char[] chars = fieldName.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : chars) {
            if (!Character.isLowerCase(c)) {
                stringBuilder.append('_');
                stringBuilder.append(Character.toLowerCase(c));
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    public static String getFieldNameByColumnName(String columnName) {
        String[] columns = columnName.split("_");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            char[] c = columns[i].toCharArray();
            if (i != 0) {
                c[0] = Character.toUpperCase(c[0]);
                result.append(c);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
