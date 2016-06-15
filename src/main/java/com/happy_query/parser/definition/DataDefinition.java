package com.happy_query.parser.definition;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by frio on 16/6/14.
 */
public class DataDefinition {
    private long id;
    /**
     * 字典名称
     */
    private String name;
    /**
     * 字段数据类型
     */
    private DataDefinitionDataType dataType;

    private DefinitionType definitionType;
    /**
     * 是否为标签类型
     */
    private boolean isTag;
    /**
     * 字段描述
     */
    private String desc;
    /**
     * 规则
     */
    private String rule;
    /**
     * 字段选项
     */
    private List<String> dataOptions;
    /**
     * 模板,控制展示,freemarker脚本
     */
    private String template;

    /**
     * 控制是否使用template
     */
    private Boolean isUseTemplate;

    /**
     * 字典类型
     */
    private String type;

    private Date gmtCreate;

    private Date gmtModified;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getUseTemplate() {
        return isUseTemplate;
    }

    public void setUseTemplate(Boolean useTemplate) {
        isUseTemplate = useTemplate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataDefinitionDataType getDataType() {
        return dataType;
    }

    public void setDataType(DataDefinitionDataType dataType) {
        this.dataType = dataType;
    }

    public boolean isTag() {
        return isTag;
    }

    public void setTag(boolean tag) {
        isTag = tag;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public List<String> getDataOptions() {
        return dataOptions;
    }

    public void setDataOptions(List<String> dataOptions) {
        this.dataOptions = dataOptions;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public DefinitionType getDefinitionType() {
        return definitionType;
    }

    public void setDefinitionType(DefinitionType definitionType) {
        this.definitionType = definitionType;
    }

    public int hashCode() {
        return Integer.valueOf(String.valueOf(id));
    }

    public boolean equals(Object obj) {
        if (obj instanceof DataDefinition){
           return ((DataDefinition) obj).getId() == this.getId();
        }
        return false;
    }

    public static DataDefinition createFromMapData(Map<String, Object> data) {
        DataDefinition dataDefinition = new DataDefinition();
        dataDefinition.setDataOptions(analysisDataOptions(data.get("data_options")));
        dataDefinition.setDataType(analysisDataDefinitionDataType(data.getOrDefault("data_type", "").toString()));
        dataDefinition.setDesc(data.getOrDefault("description", "").toString());
        dataDefinition.setDefinitionType(DefinitionType.getByValue(data.getOrDefault("definition_type", "").toString()));
        dataDefinition.setTag(data.getOrDefault("is_tag", "0").toString().equals("1") ? true : false);
        dataDefinition.setRule(data.getOrDefault("rule", "").toString());
        dataDefinition.setDataOptions(Arrays.asList(data.getOrDefault("data_options", "").toString().split(",")));
        dataDefinition.setTemplate(data.getOrDefault("template", "").toString());
        dataDefinition.setUseTemplate(data.getOrDefault("is_use_template", "0").toString().equals("1") ? true : false);
        dataDefinition.setType(data.getOrDefault("type", "0").toString());
        dataDefinition.setGmtCreate((Date) data.get("gmt_create"));
        dataDefinition.setGmtModified((Date) data.get("gmt_modified"));
        dataDefinition.setId((Long) data.get("id"));
        return dataDefinition;
    }

    public static DataDefinition createDataDefinitionById(Long id){
        DataDefinition d = new DataDefinition();
        d.setId(id);
        return d;
    }

    private static List<String> analysisDataOptions(Object data_options) {
        if (data_options != null) {
            String[] options = data_options.toString().split(",");
            return Arrays.asList(options);
        }
        return null;
    }

    public static DataDefinitionDataType analysisDataDefinitionDataType(String ddt) {
        return DataDefinitionDataType.getByValue(ddt);
    }
}
