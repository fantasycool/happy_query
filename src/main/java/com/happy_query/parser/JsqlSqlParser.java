package com.happy_query.parser;

import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.util.HappyQueryException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by frio on 16/6/15.
 */
public class JsqlSqlParser implements IJsonSqlParser {
    private IJsonLogicParser jsonLogicParser;
    private Template queryTemplate;
    private Template countTemplate;
    private static Logger LOG = LoggerFactory.getLogger(JsqlSqlParser.class);

    public JsqlSqlParser(IJsonLogicParser jsonLogicParser) {
        this.jsonLogicParser = jsonLogicParser;
        Configuration configuration = new Configuration();
        URL url = JsqlSqlParser.class.getResource("/");
        try {
            File f = new File(url.toURI());
            configuration.setDirectoryForTemplateLoading(f);
            this.queryTemplate = configuration.getTemplate("queryTemplate.sql");
            this.countTemplate = configuration.getTemplate("countTemplate.sql");
        }catch(Exception e){
           LOG.error("init JsqlSqlParser failed!", e);
        }
    }

    public String convertJsonLogicToSql(JsonParseDataParam jsonParseDataParam, String type) {
        String jsonStr = jsonParseDataParam.getJsonOperation();
        String operationStr = jsonLogicParser.convertJsonToLogicExpression(jsonStr,
                jsonParseDataParam.getPrefix(), jsonParseDataParam.getContextParameters());
        return getFreemarkerSql(jsonParseDataParam, operationStr, type);
    }

    public String getFreemarkerSql(JsonParseDataParam jsonParseDataParam, String operationStr, String type){
        Map root = new HashMap();
        root.put("left_table", jsonParseDataParam.getLeftTableName());
        root.put("right_table", jsonParseDataParam.getRightTableName());
        //control operationStr
        if(StringUtils.isBlank(operationStr)){
            operationStr = "1=1"; //set default operation;
        }
        root.put("operation_str", operationStr);
        root.put("primary_id", jsonParseDataParam.getLeftPrimaryId());
        root.put("start_index", jsonParseDataParam.getLimitStart());
        root.put("size", jsonParseDataParam.getSize());
        root.put("left_operation_str", jsonParseDataParam.getLeftOperationStr());
        //control left or right join
        root.put("connect_type", jsonParseDataParam.getConnectType() != null ? jsonParseDataParam.getConnectType() : "right");
        StringWriter sw = new StringWriter();
        try {
            if(type.equals("query")){
                queryTemplate.process(root, sw);
            }else{
                countTemplate.process(root, sw);
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
