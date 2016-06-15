package com.happy_query.parser.definition;

import java.util.List;

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
     * select, input, checkbox,multiselect
     */
    private DataDefinitionType type;
    /**
     * 字段数据类型
     */
    private DataDefinitionDataType dataType;
    /**
     * 是否为标签类型
     */
    private boolean isTag;
    /**
     * 标签类型选项
     */
    private List<String> tagOptions;
    /**
     * value
     */
    private Object value;
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

    //TODO
    /**
     * 读取template脚本,进行渲染
     * @return
     */
    public String getViewValue(){
        return "";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DataDefinitionType getType() {
        return type;
    }

    public void setType(DataDefinitionType type) {
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

    public List<String> getTagOptions() {
        return tagOptions;
    }

    public void setTagOptions(List<String> tagOptions) {
        this.tagOptions = tagOptions;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
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
}
