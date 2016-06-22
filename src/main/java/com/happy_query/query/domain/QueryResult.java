package com.happy_query.query.domain;

import com.happy_query.cache.CacheManager;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.QueryResultConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.happy_query.util.NullChecker.checkNull;

/**
 * Created by frio on 16/6/15.
 */
public class QueryResult {
    private List<Row> rows;
    private Integer count;
    private JsonParseDataParam jsonParseDataParam;
    private String querySql;
    private static Logger LOG = LoggerFactory.getLogger(QueryResult.class);

    public static QueryResult createFromOrinalData(JsonParseDataParam jsonParseDataParam,
                                                   List<Map<String, Row.Value>> originalQueryResult,
                                                   List<Map<String, Row.Value>> countQueryResult) {
        checkNull(jsonParseDataParam);
        checkNull(originalQueryResult);
        checkNull(countQueryResult);

        int countNum = Integer.valueOf(countQueryResult.get(0).get(QueryResultConstant.COUNT_COLUMN_NAME).getValue().toString());
        QueryResult queryResult = new QueryResult();
        queryResult.setCount(countNum);
        queryResult.setJsonParseDataParam(jsonParseDataParam);
        List<Row> rows = new ArrayList<Row>();

        for (int i = 0; i < originalQueryResult.size(); i++) {
            Map<String, Row.Value> m = originalQueryResult.get(i);
            Row row = new Row();
            row.setLeftTableData(m);

            /**
             * right table init
             */
            Map<String, Row.Value> rm = originalQueryResult.get(i);
            Map<DataDefinition, Row.Value> lr = new HashMap<DataDefinition, Row.Value>();
            for (Map.Entry<String, Row.Value> me : rm.entrySet()) {
                Object v = me.getValue();
                if (null == v) {
                    continue;
                }
                /**
                 * 左表判断
                 */
                if (!QueryResultConstant.DEFINITION_COLUMNS.contains(me.getKey().toString())) {
                    if (!me.getKey().equals(jsonParseDataParam.getLeftPrimaryId())) {
                        try {
                            DataDefinition leftDefinition = (DataDefinition) CacheManager.getValue(CacheManager.DEFININATION_NAME_PREFIX + me.getKey().toString());
                            Row.Value value = Row.Value.createValue(leftDefinition, me.getValue().getValue());
                            lr.put(leftDefinition, value);
                        } catch (ExecutionException e) {
                            LOG.error("get definition failed!definition name is:[{}]", me.getKey(), e);
                        }
                    }
                    continue;
                }
                String[] kvs = v.toString().split(QueryResultConstant.DEFINITION_SPLIT);
                for (String kv : kvs) {
                    String[] idValue = kv.split(QueryResultConstant.KEY_VALUE_SPIT);
                    String dataDefinitionId = idValue[0]; //definition id
                    String value = idValue[1]; //definition value

                    try {
                        Object o = CacheManager.getValue(DataDefinition.createDataDefinitionById(Long.valueOf(dataDefinitionId)));
                        if (o != null) {
                            DataDefinition dataDefinition = (DataDefinition) o;
                            Row.Value rv = Row.Value.createValue(dataDefinition, value);

                            lr.put(dataDefinition, rv);
                        }
                    } catch (ExecutionException e) {
                        LOG.error("get datadefinition value failed!", e);
                        LOG.error("datadefinition param id is: [{}]", idValue);
                    }
                }
            }
            row.setLeftId(Long.valueOf(originalQueryResult.get(i).get("left_id").toString()));
            row.setData(lr);
            rows.add(row);
        }
        queryResult.setRows(rows);
        return queryResult;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public JsonParseDataParam getJsonParseDataParam() {
        return jsonParseDataParam;
    }

    public void setJsonParseDataParam(JsonParseDataParam jsonParseDataParam) {
        this.jsonParseDataParam = jsonParseDataParam;
    }

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }

}
