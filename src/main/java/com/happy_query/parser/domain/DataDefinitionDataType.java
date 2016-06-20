package com.happy_query.parser.domain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Created by frio on 16/6/15.
 */
public enum DataDefinitionDataType {
    INT,
    STRING,
    BOOLEAN,
    DATETIME,
    DOUBLE,
    FLOAT,
    TEXT;

    private static BiMap<String, DataDefinitionDataType> biMap = HashBiMap.create();
    static{
        biMap.put("int", INT);
        biMap.put("string", STRING);
        biMap.put("bool", BOOLEAN);
        biMap.put("datetime", DATETIME);
        biMap.put("double", DOUBLE);
        biMap.put("float", FLOAT);
        biMap.put("text", TEXT);
    }

    public static DataDefinitionDataType getByValue(String v){
        return biMap.get(v);
    }

    public static String getByKey(DataDefinitionDataType d){
        return biMap.inverse().get(d);
    }

    public String toString(){
        return biMap.inverse().get(this);
    }
}
