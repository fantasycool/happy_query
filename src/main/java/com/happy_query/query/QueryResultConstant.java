package com.happy_query.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frio on 16/6/16.
 */
public interface QueryResultConstant {
    String COUNT_COLUMN_NAME = "count_num";
    String INT_GROUP_COLUMN_NAME = "int_strs";
    String DOUBLE_GROUP_COLUMN_NAME = "double_strs";
    String VARCHAR_GROUP_COLUMN_NAME = "varchar_strs";
    String FEATURE_GROUP_COLUMN_NAME = "feature_strs";
    String DEFINITION_SPLIT = "|||";
    String KEY_VALUE_SPIT = ":::";

    List<String> DEFINITION_COLUMNS = new ArrayList<String>() {{
        add(INT_GROUP_COLUMN_NAME);
        add(DOUBLE_GROUP_COLUMN_NAME);
        add(VARCHAR_GROUP_COLUMN_NAME);
        add(FEATURE_GROUP_COLUMN_NAME);
    }};

}
