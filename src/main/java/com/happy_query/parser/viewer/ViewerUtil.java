package com.happy_query.parser.viewer;

import com.happy_query.parser.definition.DataDefinition;
import com.happy_query.query.domain.Row;

/**
 * Created by frio on 16/6/15.
 */
public class ViewerUtil {
    /**
     * if DataDefinition have template set,  use template render, else return value.toString()
     * directly
     * @param dataDefinition
     * @param v
     * @return
     */
    public static String getViewValue(DataDefinition dataDefinition, Row.Value v){
        return "";
    }
}
