package com.happy_query.parser.domain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Created by frio on 16/6/15.
 */
public enum DefinitionType {
    INPUT, SELECT, MULTISELECT, CHECKBOX, DATETIME;
    private static BiMap<String, DefinitionType> biMap = HashBiMap.create();

    static {
        biMap.put("input", INPUT);
        biMap.put("select", SELECT);
        biMap.put("multiselect", MULTISELECT);
        biMap.put("checkbox", CHECKBOX);
        biMap.put("datetime", DATETIME);
    }

    public static DefinitionType getByValue(String v) {
        return biMap.get(v);
    }

    public static String getByKey(DefinitionType d) {
        return biMap.inverse().get(d);
    }

    public String toString() {
        return biMap.inverse().get(this);
    }
}
