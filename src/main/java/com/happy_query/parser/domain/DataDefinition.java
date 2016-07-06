package com.happy_query.parser.domain;

import com.google.common.base.Joiner;
import com.happy_query.parser.JsonLogicParseException;
import com.happy_query.query.domain.Row;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by frio on 16/6/14.
 */
public class DataDefinition {
    static Logger LOG = LoggerFactory.getLogger(DataDefinition.class);
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
    private Boolean isTag;
    /**
     * 字段描述
     */
    private String description;
    /**
     * 规则
     */
    private String rule;
    /**
     * 字段选项
     */
    private String dataOptions;
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

    /**
     * 字段状态
     */
    private int status;

    private String nickName;

    private String subType;

    private Boolean isEditable;

    private Boolean isLeftData;

    private String lefColName;

    private Date gmtCreate;

    private Date gmtModified;

    public Boolean getLeftData() {
        return isLeftData;
    }

    public void setLeftData(Boolean leftData) {
        isLeftData = leftData;
    }

    public String getLefColName() {
        return lefColName;
    }

    public void setLefColName(String lefColName) {
        this.lefColName = lefColName;
    }

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

    public Boolean getTag() {
        return isTag;
    }

    public void setTag(Boolean tag) {
        isTag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getDataOptions() {
        return dataOptions;
    }

    public void setDataOptions(String dataOptions) {
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

    public Boolean getEditable() {
        return isEditable;
    }

    public void setEditable(Boolean editable) {
        isEditable = editable;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DataDefinition) {
            return ((DataDefinition) obj).getId() == this.getId();
        }
        return false;
    }

    public static DataDefinition createFromMapData(Map<String, Object> data) {
        removeNullValueKey(data);
        DataDefinition dataDefinition = new DataDefinition();
        dataDefinition.setDataOptions(analysisDataOptions(data.get("data_options")));
        dataDefinition.setDataType(analysisDataDefinitionDataType(data.getOrDefault("data_type", "").toString()));
        dataDefinition.setDescription(data.getOrDefault("description", "").toString());
        dataDefinition.setDefinitionType(DefinitionType.getByValue(data.getOrDefault("definition_type", "").toString()));
        dataDefinition.setTag(data.getOrDefault("is_tag", "0").toString().equals("1") ? true : false);
        dataDefinition.setRule(data.getOrDefault("rule", "").toString());
        dataDefinition.setTemplate(data.getOrDefault("template", "").toString());
        dataDefinition.setUseTemplate(data.getOrDefault("is_use_template", "0").toString().equals("1") ? true : false);
        dataDefinition.setType(data.getOrDefault("type", "0").toString());
        dataDefinition.setGmtCreate((Date) data.get("gmt_create"));
        dataDefinition.setGmtModified((Date) data.get("gmt_modified"));
        dataDefinition.setId((Long) data.get("id"));
        dataDefinition.setStatus(0);
        dataDefinition.setSubType(data.getOrDefault("sub_type", "").toString());
        dataDefinition.setEditable(data.getOrDefault("is_editable", "0").toString().equals("1") ? true : false);
        dataDefinition.setLeftData(data.getOrDefault("is_left_data", "0").toString().equals("1") ? true : false);
        dataDefinition.setLefColName(data.getOrDefault("left_col_name", "").toString());
        dataDefinition.setNickName(data.getOrDefault("nick_name", "").toString());
        dataDefinition.setName(data.getOrDefault("name", "").toString());
        return dataDefinition;
    }

    private static void removeNullValueKey(Map<String, Object> data) {
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(data.keySet());
        for(String key : hashSet){
            if(data.get(key) == null){
                data.remove(key);
            }
        }
    }

    public static DataDefinition createDataDefinitionById(Long id) {
        DataDefinition d = new DataDefinition();
        d.setId(id);
        return d;
    }

    /**
     * don't use reflect for performance
     *
     * @return map arguments
     */
    public Map<String, Object> inverseDataDefinition() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (dataOptions != null)
            parameters.put("data_options", dataOptions);
        parameters.put("data_type", dataType.toString());
        parameters.put("description", description);
        parameters.put("definition_type", definitionType.toString());
        if (isTag != null)
            parameters.put("is_tag", isTag ? 1 : 0);
        parameters.put("rule", rule);
        if (isUseTemplate != null)
            parameters.put("is_use_template", isUseTemplate ? 1 : 0);
        if (isEditable != null)
            parameters.put("is_editable", isUseTemplate ? 1 : 0);
        if (isLeftData != null)
            parameters.put("is_left_data", isLeftData ? 1 : 0);
        parameters.put("left_col_name", lefColName);
        parameters.put("type", type);
        parameters.put("id", id);
        parameters.put("status", status);
        parameters.put("sub_type", subType);
        parameters.put("nick_name", nickName);
        return parameters;
    }

    private static String analysisDataOptions(Object data_options) {
        if (data_options != null) {
            return data_options.toString();
        }
        return null;
    }

    private static String inverseDataOptions(List<String> options) {
        return Joiner.on(",").join(options);
    }

    public static DataDefinitionDataType analysisDataDefinitionDataType(String ddt) {
        return DataDefinitionDataType.getByValue(ddt);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    /**
     * format string value to Row.Value
     *
     * @return
     */
    public Row.Value formatStringValue(String value) {
        Row.Value rv = Row.Value.createValue(null, value);
        if(StringUtils.isBlank(value)){
            rv.setDataDefinition(this);
            return rv;
        }
        try {
            switch (dataType) {
                case BOOLEAN:
                    if (value.equals("是") || value.equals("1")) {
                        rv.setValue(Integer.valueOf("1"));
                    } else {
                        rv.setValue(Integer.valueOf("0"));
                    }
                    break;
                case INT:
                    rv.setValue(Long.valueOf(value));
                    break;
                case STRING:
                    rv.setValue(value);
                    break;
                case DATETIME:
                    rv.setValue(Long.valueOf(value));
                    break;
                case FLOAT:
                    rv.setValue(Float.valueOf(value));
                    break;
                case DOUBLE:
                    rv.setValue(Double.valueOf(value));
                    break;
                case TEXT:
                    rv.setValue(String.valueOf(value));
                    break;
                default:
                    break;
            }
        }catch(NumberFormatException e){
            LOG.error("when we do number format operation,we have met an error,the type is [{}], value is [{}]",
                    this.definitionType.toString(), value, e);
            rv.setValue("-1");
        }
        if (rv.getValue() != null) {
            rv.setDataDefinition(this);
            return rv;
        }
        throw new JsonLogicParseException("data definition is not valid!");
    }

    public String toString(){
        return ReflectionToStringBuilder.toString(this);
    }

}
