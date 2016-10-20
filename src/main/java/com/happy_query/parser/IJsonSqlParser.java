package com.happy_query.parser;


import org.javatuples.Pair;

import java.util.List;

/**
 * Created by frio on 16/6/15.
 */
public interface IJsonSqlParser {
    /**
     * 将筛选规则的json转化为sql筛选的条件
     * @param json
     * @return
     *  ("and/or"),
     *  (
     *  [0]: 宽表的筛选条件
     *  [1]: 纵向指标表的筛选条件
     *  ...
     *  [n] 纵向指标的筛选条件
     *  )
     */
    Pair<String, List<String>> convertJsonToQuerySql(String json);
}
