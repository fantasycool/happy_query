package com.happy_query.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by frio on 16/6/14.
 */
public class JsonLogicParser implements IJsonLogicParser {
    private String BLANK = " ";

    public String convertJsonToLogicExpression(String json, String prefix, Map<String, String> attributesMap) {
        try {
            Object o = JSON.parse(json);
            if (o instanceof JSONArray) {
                String connector = ((JSONArray) o).get(0).toString();
                return appendOperation(connector, (JSONArray) o);
            } else {
                throw new JsonLogicParseException(String.format("invalid json logic format:%s", o.toString()));
            }
        }catch(Exception e){
            throw new JSONException("parse unexpected exception,please check your json str", e);
        }
    }

    public String appendOperation(String connector, JSONArray jsonArray) {
        StringBuilder sb = new StringBuilder();
        if (jsonArray.get(0) instanceof String) {
            for (int i = 0; i < jsonArray.size(); i++) {
                if (jsonArray.get(i) instanceof String) {
                    continue;
                } else if (jsonArray.get(i) instanceof JSONArray) {
                    sb.append("(");
                    sb.append(appendOperation(((JSONArray) jsonArray.get(i)).getString(0), (JSONArray) jsonArray.get(i)));
                    sb.append(")");
                } else {
                    JSONObject o = (JSONObject) jsonArray.get(i);
                    sb.append(o.getString("attr")).append(BLANK);
                    sb.append(o.getString("operator")).append(BLANK);
                    sb.append(o.getString("value")).append(BLANK);
                    if (i < jsonArray.size() - 1) {
                        sb.append(((String) jsonArray.get(0))).append(BLANK);
                    }
                }
            }
            return sb.toString();
        } else {
            throw new JsonLogicParseException(String.format("invalid json logic format:%s", jsonArray.toJSONString()));
        }
    }
}
