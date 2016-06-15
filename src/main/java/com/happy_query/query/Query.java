package com.happy_query.query;

import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.domain.Row;

import javax.sql.DataSource;
import java.util.List;

/**
 * Query to get result
 * Created by frio on 16/6/15.
 */
public class Query implements IQuery {
    private DataSource dataSource;

    public Query(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public List<Row> queryByJsonLogic(JsonParseDataParam jsonParseDataParam) {
        return null;
    }
}
