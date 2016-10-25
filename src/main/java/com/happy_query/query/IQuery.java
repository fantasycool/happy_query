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
     * Get Prm User Info by prmId and keys
     * @param prmId
     * @param keys
     * @param connection we use this to for transaction, if we don't need this, put it null
     * @return
     */
    Map<String, Object> getPrmUserInfo(Long prmId, List<String> keys, Connection connection);

    /**
     * Query Prm User Infos by json query
     * @param jsonQuery
     * @param start
     * @param size
     * @return
     */
    Pair<Integer, List<Map<String, Object>>> queryPrmUserInfosByJson(String jsonQuery, int start, int size);
}
