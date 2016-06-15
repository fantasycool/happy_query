package com.happy_query.query;

import com.happy_query.parser.IJsonSqlParser;
import com.happy_query.parser.JsqlSqlParser;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.domain.HappyQueryResult;
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

    public Query(DataSource dataSource){
        this.dataSource = dataSource;
        this.jsonSqlParser = new JsqlSqlParser();
    }

    public HappyQueryResult queryByJsonLogic(JsonParseDataParam jsonParseDataParam) {
        if(!jsonParseDataParam.check()){
            throw new IllegalArgumentException("invalid jsonParseDataParam");
        }
        HappyQueryResult happyQueryResult = new HappyQueryResult();

        String querySql = jsonSqlParser.convertJsonLogicToQuerySql(jsonParseDataParam);
        String countSql = jsonSqlParser.convertJsonLogicToCountSql(jsonParseDataParam);
        try {
            List<Map<String, Object>> originalQueryResult = JDBCUtils.executeQuery(dataSource, querySql, null);
        } catch (SQLException e) {
            throw new QueryException("query sql exception", e);
        }
        return null;
    }
}
