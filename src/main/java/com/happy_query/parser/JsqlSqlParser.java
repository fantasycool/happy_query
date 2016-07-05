package com.happy_query.parser;

import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.util.HappyQueryException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by frio on 16/6/15.
 */
public class JsqlSqlParser implements IJsonSqlParser {
    private IJsonLogicParser jsonLogicParser;
    private Template queryTemplate;
    private Template andQueryTemplate;
    private Template andCountTemplate;
    private Template countTemplate;
    static String And_Match_Pattern = "\\(.*?\\)\\s+and\\s+";
    static String Find_Match_Pattern = "\\(.*?\\)";

    private static Logger LOG = LoggerFactory.getLogger(JsqlSqlParser.class);

    public JsqlSqlParser(IJsonLogicParser jsonLogicParser) {
        this.jsonLogicParser = jsonLogicParser;
        Configuration configuration = new Configuration();
        try {
            configuration.setClassForTemplateLoading(this.getClass(), "/");
            this.queryTemplate = configuration.getTemplate("queryTemplate.sql");
            this.countTemplate = configuration.getTemplate("countTemplate.sql");
            this.andQueryTemplate = configuration.getTemplate("andQueryTemplate.sql");
            this.andCountTemplate = configuration.getTemplate("andCountTemplate.sql");
        } catch (Exception e) {
            LOG.error("init JsqlSqlParser failed!", e);
            e.printStackTrace();
        }
    }

    public String convertJsonLogicToSql(JsonParseDataParam jsonParseDataParam, String type) {
        String jsonStr = jsonParseDataParam.getJsonOperation();
        StringBuilder leftStringBuilder = new StringBuilder();
        String operationStr = jsonLogicParser.convertJsonToLogicExpression(jsonStr,
                jsonParseDataParam.getPrefix(), jsonParseDataParam.getContextParameters(), leftStringBuilder);
        //dig up left column query expression
        if(StringUtils.isNotBlank(leftStringBuilder.toString())){
            jsonParseDataParam.setLeftOperationStr(leftStringBuilder.toString() + " 1=1");
        }
        //add leftId in operation
        if (jsonParseDataParam.getLeftIds() != null) {
            operationStr = getLeftIdsInStr(jsonParseDataParam.getLeftIds()) + " and " + operationStr;
        }
        return getFreemarkerSql(jsonParseDataParam, operationStr, type);
    }

    private String getLeftIdsInStr(List<Long> leftIds) {
        StringBuilder sb = new StringBuilder();
        sb.append("bb.left_id in (");
        for (int i = 0; i < leftIds.size(); i++) {
            sb.append(leftIds.get(i));
            if (i < leftIds.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public String getFreemarkerSql(JsonParseDataParam jsonParseDataParam, String operationStr, String type) {
        Map root = new HashMap();
        root.put("left_table", jsonParseDataParam.getLeftTableName());
        root.put("right_table", jsonParseDataParam.getRightTableName());
        Pattern forceP = Pattern.compile("(int|double|str)_value");
        Matcher forceM = forceP.matcher(operationStr);
        String forceIndex = null;
        if(forceM.find()){
            forceIndex  = "force index(" + forceM.group(1) + "_index)";
            root.put("force_index", forceIndex);
        }
        //control operationStr
        boolean isLeft = false;
        if (StringUtils.isBlank(operationStr)) {
            operationStr = "1=1"; //set default operation;
            root.put("only_left", true);
            isLeft = true;
        }
        root.put("operation_str", operationStr);
        /**
         * now we do generate join strs
         */
        String matchStr = operationStr;
        Pattern pattern = Pattern.compile(And_Match_Pattern);
        Matcher m = pattern.matcher(matchStr);
        boolean isAndQuery = false;
        if (m.find()) {
            //ok, that's an and operation
            StringBuilder joinStr = new StringBuilder();
            pattern = Pattern.compile(Find_Match_Pattern);
            isAndQuery = true;
            m = pattern.matcher(matchStr);
            int counter = 0;
            while (m.find()) {
                root.remove("force_index"); /** we don't force to use index when we have multiple conditions **/
                String tableName = "z" + ++counter + "";
                String operation = m.group();
                operation = operation.replace("bb.", tableName + ".").replace("aa.", tableName + ".");
                joinStr.append(String.format(" JOIN data_definition_value %s on " +
                        "aa.left_id = %s.left_id and %s", tableName, tableName, operation));
            }
            root.put("join_str", joinStr.toString());
        }

        root.put("primary_id", jsonParseDataParam.getLeftPrimaryId());
        root.put("start_index", jsonParseDataParam.getLimitStart());
        root.put("size", jsonParseDataParam.getSize());
        if (StringUtils.isBlank(jsonParseDataParam.getLeftOperationStr()) && !isLeft) {
            root.put("only_right", true);
        }
        root.put("left_operation_str", jsonParseDataParam.getLeftOperationStr());
        //control left or right join
        root.put("connect_type", jsonParseDataParam.getConnectType() != null ? jsonParseDataParam.getConnectType() : "right");
        StringWriter sw = new StringWriter();
        try {
            if (type.equals("query")) {
                if (isAndQuery) {
                    andQueryTemplate.process(root, sw);
                }else{
                    queryTemplate.process(root, sw);
                }
            } else {
                if (isAndQuery) {
                    andCountTemplate.process(root, sw);
                }else{
                    countTemplate.process(root, sw);
                }
            }
        } catch (TemplateException e) {
            LOG.error("sql template render failed!param= [{}]", jsonParseDataParam.toString());
            throw new HappyQueryException(e);
        } catch (IOException e) {
            LOG.error("template file read io failed!param=[{}]", jsonParseDataParam.toString());
            throw new HappyQueryException(e);
        }
        return sw.toString();
    }

    public String convertJsonLogicToQuerySql(JsonParseDataParam jsonParseDataParam) {
        return convertJsonLogicToSql(jsonParseDataParam, "query");
    }

    public String convertJsonLogicToCountSql(JsonParseDataParam jsonParseDataParam) {
        return convertJsonLogicToSql(jsonParseDataParam, "count");
    }
}
