package com.happy_query.query;

import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.domain.Row;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by frio on 16/6/15.
 */
public class Query implements IQuery {
    public List<Row> queryByJsonLogic(JsonParseDataParam jsonParseDataParam, DataSource dataSource) {
        return null;
    }
}
