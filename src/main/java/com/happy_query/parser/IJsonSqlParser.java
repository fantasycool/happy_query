package com.happy_query.parser;

import com.happy_query.parser.domain.JsonParseDataParam;

/**
 * Created by frio on 16/6/15.
 */
public interface IJsonSqlParser {
    /**
     * Get query sql from json parse data
     * @param jsonParseDataParam
     * @return
     */
    String convertJsonLogicToSql(JsonParseDataParam jsonParseDataParam);
}
