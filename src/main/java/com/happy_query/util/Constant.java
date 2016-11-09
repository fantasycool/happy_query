package com.happy_query.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by frio on 16/6/20.
 */
public class Constant {
    public static final String AND = "and";
    public static final String OR = "or";
    public static final String RANGE = "range";
    public static final String CONTAINS = "contains";
    public static final String EQUALS = "equals";
    public static final String VALUE = "value";
    public static final String CHECKBOX = "checkbox";
    public static final String OPERATOR_VALUE_RANGE = "range";
    public static final String OPERATOR_VALUE_CONTAINS = "contains";
    public static final String OPERATOR_VALUE_EQUALS = "equals";
    public static final String OPERATOR = "operator";
    /**
     * 页面展示使用
     */
    public static final int VIEW_GROUP_TYPE = 2;
    /**
     * table data_definition_value category->sub_key values
     */
    public static Map<String, Integer> SUB_KEY_MAP = new HashMap<String, Integer>();
    public static String USER_CATEGORY_TYPE = "user";
    public static String FUWU_CATEGORY_TYPE = "service";
    public static String MOCK_CATEGORY_TYPE = "mock";
    public static String TABLE_NAME = "statistic_keys";

    static {
        SUB_KEY_MAP.put("user", 0);
        SUB_KEY_MAP.put("service", 1);
        SUB_KEY_MAP.put("mock", 2);
    }

    /**
     * table data_definition_value category->left_table_name values
     */
    public static Map<String, String> LEFT_TABLE_MAP = new HashMap<String, String>();

    static {
        LEFT_TABLE_MAP.put("user", "customer");
        LEFT_TABLE_MAP.put("service", "contact_record");
        LEFT_TABLE_MAP.put("mock", "left_table");
    }

    /**
     * table data_definition_value category->right_table_name values
     */
    public static Map<String, String> RIGHT_TABLE_MAP = new HashMap<String, String>();

    static {
        RIGHT_TABLE_MAP.put("user", "data_definition_value");
        RIGHT_TABLE_MAP.put("service", "data_definition_value");
        RIGHT_TABLE_MAP.put("mock", "data_definition_value");
    }

    /**
     * left table category->primary id column name
     */
    public static Map<String, String> LEFT_ID_COLUMNS = new HashMap<String, String>();
    static {
        LEFT_ID_COLUMNS.put("user", "cus_id");
        LEFT_ID_COLUMNS.put("service", "contact_id");
        LEFT_ID_COLUMNS.put("mock", "id");
    }

    //new added

    public static String PRM_USER_INFO = "prm_user_info";
    public static String DATA_DEFINITION_VALUE = "data_definition_value";
    public static String PRM_TAG_KEY_RELATION = "prm_tag_key_relation";
    public static String COMMENT_PREFIX = "_comment";
    public static String KEY_RELATION_TABLE_NAME = "prm_key_relation";
    public static String PRM_DATA_OPTIONS = "prm_data_options";

    public static final int TAG_TYPE = 1;
    public static final int COMMENT_TYPE = 2;
    public static final int NORMAL_TYPE = 0;

    public static final int XI_TONG_BIAO_QIAN = 1;
    public static final int HAND_BIAO_QIAN = 0;
    public static final int GROUP_BIAO_QIAN = 3;
    public static final int DYNAMIC_BIAO_QIAN = 2;
    public static String HAPPY_QUERY_ERROR_RULE_OVERRIDE = "HAPPY_QUERY_ERROR_RULE_OVERRIDE";

    /**
     * tag task status
     */
    public static final int TAG_TASK_STATUS_CREATED = 0;
    public static final int TAG_TASK_STATUS_STARTED = 1;
    public static final int TAG_TASK_STATUS_SUCCESS = 2;
    public static final int TAG_TASK_STATUS_FAILED = -1;
}
