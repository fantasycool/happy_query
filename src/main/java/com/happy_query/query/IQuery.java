package com.happy_query.query;

import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.domain.QueryResult;

/**
 * Query Runner to get query Result
 * Created by frio on 16/6/15.
 */
public interface IQuery {
    /**
     * Query By JsonParseDataParam
     * columns result from data_definition_value table
     * cannot set 'order by [dynamic_column]' expression.
     *
     * @param jsonParseDataParam
     * @return
     */
    QueryResult queryByJsonLogic(JsonParseDataParam jsonParseDataParam);

    /**
     * Get by left table id and category type
     * @param leftId
     * @param categoryType
     * @return
     */
    QueryResult queryByLeftId(long leftId, String categoryType);
}
