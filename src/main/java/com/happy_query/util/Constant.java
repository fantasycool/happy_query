package com.happy_query.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frio on 16/6/20.
 */
public class Constant {
    /**
     * table data_definition_value category->sub_key values
     */
    public static Map<String, Integer> SUB_KEY_MAP = new HashMap<String, Integer>();

    static {
        SUB_KEY_MAP.put("user", 0);
        SUB_KEY_MAP.put("service", 1);
    }

    /**
     * table data_definition_value category->left_table_name values
     */
    public static Map<String, String> LEFT_TABLE_MAP = new HashMap<String, String>();

    static {
        LEFT_TABLE_MAP.put("user", "customer");
        LEFT_TABLE_MAP.put("service", "customer");
    }

    /**
     * table data_definition_value category->right_table_name values
     */
    public static Map<String, String> RIGHT_TABLE_MAP = new HashMap<String, String>();

    static {
        RIGHT_TABLE_MAP.put("user", "data_definition_value");
        RIGHT_TABLE_MAP.put("service", "data_definition_value");
    }

    /**
     * left table category->primary id column name
     */
    public static Map<String, String> LEFT_ID_COLUMNS = new HashMap<String, String>();
    static {
        LEFT_ID_COLUMNS.put("user", "id");
        LEFT_ID_COLUMNS.put("service", "id");
    }
}
