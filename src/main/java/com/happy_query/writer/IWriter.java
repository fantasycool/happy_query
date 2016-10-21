package com.happy_query.writer;

import com.happy_query.util.HappyQueryException;

import java.util.Map;

/**
 * Created by frio on 16/6/16.
 */
public interface IWriter {

    /**
     * 指标数据新增
     * 指标key-> 指标值
     * @param keyDatas
     * @param userKey
     * @param source 用户来源(zsty|other)
     * @param empName 操作人
     */
    long insertRecord(Map<String, Object> keyDatas, String source, String userKey, String empName)throws HappyQueryException;

    /**
     * 指标数据更新
     * @param keyDatas
     * @param prmId
     * @param empName
     * @throws HappyQueryException
     */
    void updateRecord(Map<String, Object> keyDatas, long prmId, String empName)throws HappyQueryException;


}
