package com.happy_query.parser;

import java.util.Map;

/**
 * Created by frio on 16/6/14.
 */
public interface IJsonLogicParser {

    /**
     * eg:
     * JSON format:
     * [
     * "and" || "or",
     * {
     * "attr":"a",
     * "operator":">",
     * "value":"1"
     * },
     * {
     * "attr":"b",
     * "operator": "<",
     * "value":"10"
     * },
     * [
     * "or",
     * {
     * "attr":"c",
     * "operator":"in",
     * "value":"(1,2,3,4)"
     * },
     * {
     * "attr":"d",
     * "operator":"=",
     * "value":"10"
     * }
     * ]
     * ]
     * <p>
     * Convert to logic expression:
     * a > 1 and b < 10 and ((c in (1,2,3,4)) or d=10)
     *
     * @param json
     * @param prefix        when converting, add prefix to attribute name(if attributesMap arg is used,
     *                      prefix will be added after attribute converted to value)
     * @param attributesMap when converting, replace attribute to map value
     * @return
     */
    String convertJsonToLogicExpression(String json, String prefix, Map<String, String> attributesMap);
}
