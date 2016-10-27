package com.happy_query.query;

import org.javatuples.Pair;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Query Runner to get query Result
 * Created by frio on 16/6/15.
 */
public interface IQuery {

    /**
     * 根据用户id和指标keys列表获取用户指标信息
     * @param prmId
     * @param keys
     * @param connection we use this to for transaction, if we don't need this, put it null
     * @return
     */
    Map<String, Object> getPrmUserInfo(Long prmId, List<String> keys, Connection connection);

    /**
     * 传入筛选json条件筛选
     * @param jsonQuery
     * @param start
     * @param size
     * @return Pair[符合条件的总数; 指标列表(只有横向表的数据)]
     */
    Pair<Integer, List<Map<String, Object>>> queryPrmUserInfosByJson(String jsonQuery, int start, int size);
}
