package com.happy_query.query;

import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.domain.HappyQueryResult;

/**
 * Query Runner to get query Result
 * Created by frio on 16/6/15.
 */
public interface IQuery {
    /**
     * Query By JsonParseDataParam
     * @param jsonParseDataParam
     * @return
     */
    HappyQueryResult queryByJsonLogic(JsonParseDataParam jsonParseDataParam);
}
