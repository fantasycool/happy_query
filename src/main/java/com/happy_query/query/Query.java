package com.happy_query.query;

import com.happy_query.cache.CacheManager;
import com.happy_query.parser.IJsonLogicParser;
import com.happy_query.parser.IJsonSqlParser;
import com.happy_query.parser.JsonLogicParser;
import com.happy_query.parser.JsqlSqlParser;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.domain.QueryResult;
import com.happy_query.query.domain.Row;
import com.happy_query.util.Constant;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.HappyQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Query to get result
 * Created by frio on 16/6/15.
 */
public class Query implements IQuery {
    private DataSource dataSource;
    private IJsonSqlParser jsonSqlParser;
    static Logger LOG = LoggerFactory.getLogger(Query.class);

    public Query(DataSource dataSource) {
        this.dataSource = dataSource;
        IJsonLogicParser jsonLogicParser = new JsonLogicParser();
        this.jsonSqlParser = new JsqlSqlParser(jsonLogicParser);
        CacheManager.dataSource = dataSource;
    }

    public QueryResult queryByJsonLogic(JsonParseDataParam jsonParseDataParam) {
        if (!jsonParseDataParam.check()) {
            throw new IllegalArgumentException("invalid jsonParseDataParam");
        }

        String querySql = jsonSqlParser.convertJsonLogicToQuerySql(jsonParseDataParam);
        String countSql = jsonSqlParser.convertJsonLogicToCountSql(jsonParseDataParam);
        System.out.println(querySql);
        System.out.println(countSql);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            JDBCUtils.execute(connection, "SET SESSION group_concat_max_len = 1000000", new ArrayList<Object>());
            long t1 = System.currentTimeMillis();
            List<Map<String, Row.Value>> originalQueryResult = JDBCUtils.executeQuery(connection, querySql, new ArrayList(0));
            System.out.println(String.format("query time is:%d" ,System.currentTimeMillis() - t1));
            List<Map<String, Row.Value>> countQueryResult = JDBCUtils.executeQuery(connection, countSql, new ArrayList(0));
            System.out.println(String.format("all time is:%d" ,System.currentTimeMillis() - t1));
            LOG.info("##############happy query executing time ############### time:[{}]", System.currentTimeMillis() - t1);
            System.out.println(String.format("##############happy query executing time ############### time:[%d]", System.currentTimeMillis() - t1));
            QueryResult queryResult = QueryResult.createFromOrinalData(jsonParseDataParam, originalQueryResult, countQueryResult);
            return queryResult;
        } catch (SQLException e) {
            LOG.error("querySqlIs:[{}], countSqlIs:[{}]", querySql, countSql);
            System.out.println(String.format("querySqlIs:%s, countSqlIs:%s", querySql, countSql));
            throw new HappyQueryException("query sql exception", e);
        } finally {
            JDBCUtils.close(connection);
        }
    }

    public QueryResult queryByLeftId(long leftId, String categoryType) {
        String rightTable = Constant.RIGHT_TABLE_MAP.get(categoryType);
        String leftTable = Constant.LEFT_TABLE_MAP.get(categoryType);
        String leftIdColumn = Constant.LEFT_ID_COLUMNS.get(categoryType);

        JsonParseDataParam jsonParseDataParam = new JsonParseDataParam();
        jsonParseDataParam.setLeftTableName(leftTable);
        jsonParseDataParam.setRightTableName(rightTable);
        jsonParseDataParam.setLeftPrimaryId(leftIdColumn);
        jsonParseDataParam.setLimitStart(0);
        jsonParseDataParam.setSize(1);
        jsonParseDataParam.setLeftOperationStr(leftIdColumn + "=" + leftId);
        jsonParseDataParam.setConnectType("left");

        String querySql = jsonSqlParser.getFreemarkerSql(jsonParseDataParam, "bb.left_id=" + leftId, "query");
        String countSql = jsonSqlParser.getFreemarkerSql(jsonParseDataParam, "bb.left_id=" + leftId, "count");
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            JDBCUtils.execute(connection, "SET SESSION group_concat_max_len = 1000000", new ArrayList<Object>());
            List<Map<String, Row.Value>> queryResult = JDBCUtils.executeQuery(connection, querySql, new ArrayList(0));
            List<Map<String, Row.Value>> countResult = JDBCUtils.executeQuery(connection, countSql, new ArrayList(0));
            return QueryResult.createFromOrinalData(jsonParseDataParam, queryResult, countResult);
        } catch (SQLException e) {
            throw new HappyQueryException("query by leftId:" + leftId + "failed", e);
        } finally {
            JDBCUtils.close(connection);
        }
    }
}
