package com.happy_query.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.DataOption;
import com.happy_query.parser.domain.DefinitionType;
import com.happy_query.query.domain.Row;
import com.happy_query.writer.DefinitionException;
import com.happy_query.writer.HappyWriterException;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * data from
 * 1. view->storage
 * 2. storage->view
 * Created by frio on 16/7/8.
 */
public class Transformer {
    static Logger LOG = LoggerFactory.getLogger(Transformer.class);

    public static Object undress(DataDefinition d, String s, Function function){
        try {
            Object result = null;
            //operation on null
            if (StringUtils.isNullOrEmpty(s)) {
                switch (d.getDataType()) {
                    case INT:
                        result = 0;
                    case STRING:
                        result = null;
                    case BOOLEAN:
                        result = 0;
                    case DATETIME:
                        result = 0;
                    case DOUBLE:
                        result = 0;
                    case FLOAT:
                        result = 0;
                    case TEXT:
                        result = null;
                }
                return result;
            }
            if (d.getDefinitionType() == DefinitionType.SELECT) {
                result = getCodeFromValue(d.getDataOptionList(), s);
            } else if (d.getDefinitionType() == DefinitionType.DATETIME) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    result = simpleDateFormat.parse(s).getTime();
                } catch (ParseException e) {
                    throw new HappyWriterException("invalid time format:" + s);
                }
            } else if (d.getTag()) {
                result = d.getId();
            } else if (d.getDefinitionType() == DefinitionType.MULTISELECT || d.getDefinitionType() == DefinitionType.CHECKBOX) {
                result = getMultiSelectJson(d, s);
            } else {
                result = function.reverseRender(s, d.getId(), "import");
            }
            return result;
        }catch(Exception e){
            throw new DefinitionException(String.format("指标[%s]的解析出现问题,请确认数据格式是否正确", d.getNickName()));
        }
    }

    private static Object getMultiSelectJson(DataDefinition d, String s) {
        try{
            List<DataOption> dataOptions = d.getDataOptionList();
            Map<String, Boolean> result = new HashMap<String, Boolean>();
            for(DataOption dataOption : dataOptions){
                boolean isSelect = false;
                for(String v : s.split("\\s")){
                    if(v.equals(dataOption.getValue())){
                        result.put(dataOption.getCode(), true);
                        isSelect = true;
                    }
                }
                if(!isSelect){
                    result.put(dataOption.getCode(), false);
                }
            }
            return JSON.toJSONString(result);
        }catch(Exception e){
            LOG.error("get multi select json failed, definition:[{}], s:[{}], we return null value",
                    d.toString(), s, e);
            return null;
        }
    }

    public static String dressUp(DataDefinition d, Object o, Function function){
        String viewStr = "无";
        if(null == o){
            return viewStr;
        }
        if (d.getDefinitionType() == DefinitionType.SELECT) {
            viewStr = getValueFromCode(d.getDataOptionList(), o);
        }else if(d.getDefinitionType() == DefinitionType.MULTISELECT || d.getDefinitionType() == DefinitionType.CHECKBOX){
            viewStr = dressOnMultiSelectData(d, o);
        } else if (d.getDefinitionType() == DefinitionType.DATETIME) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            viewStr = simpleDateFormat.format(new Date(Long.valueOf(o.toString())));
        } else if (d.getTag()) {
            viewStr = d.getNickName();
        } else {
            viewStr = function.render(Row.Value.createValue(d, o), d.getId(), "export").toString();
        }
        return "";
    }

    /**
     * when we do export, we use this
     * @param d
     * @param o
     * @return
     */
    private static String dressOnMultiSelectData(DataDefinition d, Object o) {
        JSONObject jsonObject = (JSONObject) JSON.parse(o.toString());
        Joiner joiner = Joiner.on(" ");
        List<String> list = new ArrayList<String>();
        for(Map.Entry e : jsonObject.entrySet()){
            String key = e.getKey().toString();
            Boolean value = Boolean.valueOf(e.getValue().toString());
            if(value){
               String vv = getValueFromCode(d.getDataOptionList(), key);
                list.add(vv);
            }
        }
        return joiner.join(list);
    }

    /**
     * Get Value from Code
     * @param dataOptions
     * @param value
     * @return
     */
    private static String getValueFromCode(List<DataOption> dataOptions, Object value) {
        for(DataOption d : dataOptions){
            if(d.getCode().equals(value.toString())){
                return d.getValue();
            }
        }
        return value.toString();
    }

    /**
     * Get Code from Value
     * @return
     */
    private static Object getCodeFromValue(List<DataOption> dataOptions, String value){
        for(DataOption d : dataOptions){
            if(d.getValue().equals(value.toString())){
                return d.getCode();
            }
        }
        return value;
    }
}
