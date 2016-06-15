package com.happy_query.query;

import com.happy_query.parser.IJsonSqlParser;
import com.happy_query.parser.JsqlSqlParser;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.domain.HappyQueryResult;

import javax.sql.DataSource;

/**
 * Query to get result
 * Created by frio on 16/6/15.
 */
public class Query implements IQuery {
    private DataSource dataSource;
    private IJsonSqlParser jsonSqlParser;

    public Query(DataSource dataSource){
        this.dataSource = dataSource;
        this.jsonSqlParser = new JsqlSqlParser();
    }

    public HappyQueryResult queryByJsonLogic(JsonParseDataParam jsonParseDataParam) {
        String querySql = jsonSqlParser.convertJsonLogicToQuerySql(jsonParseDataParam);
        return null;
    }
}
