package com.happy_query.query;

import com.happy_query.cache.CacheManager;
import com.happy_query.parser.IJsonLogicParser;
import com.happy_query.parser.IJsonSqlParser;
import com.happy_query.parser.JsonLogicParser;
import com.happy_query.parser.JsqlSqlParser;
import com.happy_query.parser.definition.DataDefinition;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.domain.QueryResult;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.QueryException;

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
    private List<DataDefinition> dataDefinitions;

    public Query(DataSource dataSource){
        this.dataSource = dataSource;
        CacheManager cacheManager = new CacheManager(dataSource);
        IJsonLogicParser jsonLogicParser = new JsonLogicParser(cacheManager);
        this.jsonSqlParser = new JsqlSqlParser(jsonLogicParser);
    }

    public void init(){

    }

    public QueryResult queryByJsonLogic(JsonParseDataParam jsonParseDataParam) {
        if(!jsonParseDataParam.check()){
            throw new IllegalArgumentException("invalid jsonParseDataParam");
        }

        String querySql = jsonSqlParser.convertJsonLogicToQuerySql(jsonParseDataParam);
        String countSql = jsonSqlParser.convertJsonLogicToCountSql(jsonParseDataParam);
        try {
            List<Map<String, Object>> originalQueryResult = JDBCUtils.executeQuery(dataSource, querySql, null);
            List<Map<String, Object>> countQueryResult = JDBCUtils.executeQuery(dataSource, countSql, null);
            QueryResult queryResult = QueryResult.createFromOrinalData(jsonParseDataParam, originalQueryResult, countQueryResult);
            return queryResult;
        } catch (SQLException e) {
            throw new QueryException("query sql exception", e);
        }
    }
}
