package com.happy_query.query;

import com.happy_query.cache.CacheManager;
import com.happy_query.parser.IJsonLogicParser;
import com.happy_query.parser.IJsonSqlParser;
import com.happy_query.parser.JsonLogicParser;
import com.happy_query.parser.JsqlSqlParser;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.domain.QueryResult;
import com.happy_query.query.domain.Row;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.HappyQueryException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Query to get result
 * Created by frio on 16/6/15.
 */
public class Query implements IQuery {
    private DataSource dataSource;
    private IJsonSqlParser jsonSqlParser;

    public Query(DataSource dataSource){
        this.dataSource = dataSource;
        IJsonLogicParser jsonLogicParser = new JsonLogicParser();
        this.jsonSqlParser = new JsqlSqlParser(jsonLogicParser);
        CacheManager.dataSource = dataSource;
    }

    public QueryResult queryByJsonLogic(JsonParseDataParam jsonParseDataParam) {
        if(!jsonParseDataParam.check()){
            throw new IllegalArgumentException("invalid jsonParseDataParam");
        }

        String querySql = jsonSqlParser.convertJsonLogicToQuerySql(jsonParseDataParam);
        String countSql = jsonSqlParser.convertJsonLogicToCountSql(jsonParseDataParam);
        try {
            //remember to set "SET SESSION group_concat_max_len = 1000000";
            List<Map<String, Row.Value>> originalQueryResult = JDBCUtils.executeQuery(dataSource, querySql, null);
            List<Map<String, Row.Value>> countQueryResult = JDBCUtils.executeQuery(dataSource, countSql, null);
            QueryResult queryResult = QueryResult.createFromOrinalData(jsonParseDataParam, originalQueryResult, countQueryResult);
            return queryResult;
        } catch (SQLException e) {
            throw new HappyQueryException("query sql exception", e);
        }
    }
}
