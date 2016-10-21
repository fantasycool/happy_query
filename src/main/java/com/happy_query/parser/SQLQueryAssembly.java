package com.happy_query.parser;

import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by frio on 16/10/20.
 */
public class SQLQueryAssembly {
    private static Logger LOG = LoggerFactory.getLogger(SQLQueryAssembly.class);
    private static Template queryTemplate;

    static {
        Configuration configuration = new Configuration();
        try {
            configuration.setClassForTemplateLoading(SQLQueryAssembly.class, "/");
            queryTemplate = configuration.getTemplate("querySqlAssemble.sql");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("init SQLQueryAssembly failed!", e);
        }
    }

    /**
     * 指标条件SQL拼装
     *
     * @param p
     * @return query sql
     */
    public static String assemblyQuerySql(Pair<String, List<String>> p, int start, int limit, boolean isQuery) {
        Map root = new HashMap();
        root.put("start_index", start);
        root.put("size", limit);
        root.put("primary_id", "prm_id");
        root.put("left_table", Constant.PRM_USER_INFO);
        if (isQuery) {
            root.put("is_query", true);
        }
        if (p.getValue1().size() == 1) {
            root.put("only_left", true);
            if (StringUtils.isNotBlank(p.getValue1().get(0))) {
                root.put("left_operation_str", p.getValue1().get(0));
            }
        } else if (StringUtils.isBlank(p.getValue0()) && p.getValue1().size() > 0) {
            root.put("only_right", true);
            String joinStr = assemblyJoinStr(p);
            root.put("join_str", joinStr);
        } else {
            if (StringUtils.isNotBlank(p.getValue1().get(0))) {
                root.put("left_operation_str", p.getValue1().get(0));
            }
            String joinStr = assemblyJoinStr(p);
            root.put("join_str", joinStr);
        }
        StringWriter sw = new StringWriter();
        try {
            queryTemplate.process(root, sw);
        } catch (TemplateException e) {
            throw new HappyQueryException("query freemarker Template", e);
        } catch (IOException e) {
            throw new HappyQueryException(e);
        }
        return sw.toString();
    }

    private static String assemblyJoinStr(Pair<String, List<String>> p) {
        StringBuilder sb = new StringBuilder();
        String ALIAS_PREFIX = "z_";
        for (int i = 1; i < p.getValue1().size(); i++) {
            String ALIS_TABLE_NAME = ALIAS_PREFIX + i;
            if (p.getValue0().equals("and")) {
                sb.append(String.format("\n JOIN data_definition_value %s on " +
                        "p.prm_id = %s.prm_id and %s", ALIS_TABLE_NAME, ALIS_TABLE_NAME, p.getValue1().get(i)).replace("#{prefix}", ALIS_TABLE_NAME + "."));
            } else if (p.getValue0().equals("or")) {
                if (i == 1) {
                    sb.append(" where ");
                }
                sb.append(p.getValue1().get(i).replace("#{prefix}", "p."));
                if (i != p.getValue1().size() - 1) {
                    sb.append("\n or ");
                }
            } else {
                throw new HappyQueryException("invalid connector type");
            }
        }
        return sb.toString();
    }
}
