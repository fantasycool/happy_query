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
    String convertJsonLogicToQuerySql(JsonParseDataParam jsonParseDataParam);

    /**
     * Get count sql from json parse data
     * @param jsonParseDataParam
     * @return
     */
    String convertJsonLogicToCountSql(JsonParseDataParam jsonParseDataParam);
}
