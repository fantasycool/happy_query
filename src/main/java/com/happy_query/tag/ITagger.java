package com.happy_query.tag;

/**
 * 异步打标任务提交处
 * Created by frio on 16/11/3.
 */
public interface ITagger {
    /**
     * 异步打标任务提交
     * @param tagKey
     * @param queryExpression
     */
    void tagDatas(String tagKey, String queryExpression);

}
