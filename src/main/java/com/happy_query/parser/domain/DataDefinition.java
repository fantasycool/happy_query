package com.happy_query.parser.domain;

import com.happy_query.util.HappyQueryException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by frio on 16/6/14.
 */
public class DataDefinition {
    static Logger LOG = LoggerFactory.getLogger(DataDefinition.class);
    private long id;
    private Long parentId;
    private Integer type;
    private String childCommentName;
    private Integer updateType;
    private Integer rank;
    private String computationRule;
    private Integer tagType;
    private String definitionType;
    private String dataType;
    private DataDefinition parent;
    private DataDefinition childComment;

    /**
     * 字典名称
     */
    private String key;

    /**
     * 字段数据类型
     */
    private DataDefinitionDataType dataTypeEnum;

    private DefinitionType definitionTypeEnum;
    /**
     * 是否支持筛选
     */
    private Boolean isQuery;
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
     * 字段状态
     */
    private String unit;

    private String sourceData;

    private int status;

    private String nickName;

    private Boolean isEditable;

    private Boolean isLeftData;

    private String lefColName;

    private Date gmtCreate;

    private Date gmtModified;

    private String isRequired;

    public String getComputationRule() {
        return computationRule;
    }

    public void setComputationRule(String computationRule) {
        this.computationRule = computationRule;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getIsRequired() {
        return isRequired;
    }

    public String getSourceData() {
        return sourceData;
    }

    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }

    public void setIsRequired(String isRequired) {
        this.isRequired = isRequired;
    }

    public String getUnit() {
        return unit;
    }

    public Integer getUpdateType() {
        return updateType;
    }

    public void setUpdateType(Integer updateType) {
        this.updateType = updateType;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

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

    public Boolean getUseTemplate() {
        return isUseTemplate;
    }

    public void setUseTemplate(Boolean useTemplate) {
        isUseTemplate = useTemplate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataDefinitionDataType getDataTypeEnum() {
        return dataTypeEnum;
    }

    public void setDataTypeEnum(DataDefinitionDataType dataTypeEnum) {
        this.dataTypeEnum = dataTypeEnum;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public DefinitionType getDefinitionTypeEnum() {
        return definitionTypeEnum;
    }

    public void setDefinitionTypeEnum(DefinitionType definitionTypeEnum) {
        this.definitionTypeEnum = definitionTypeEnum;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DataDefinition) {
            return ((DataDefinition) obj).getId() == this.getId();
        }
        return false;
    }


    public int getStatus() {
        return status;
    }

    public void initEnum(){
        this.definitionTypeEnum = DefinitionType.getByValue(definitionType);
        this.dataTypeEnum = DataDefinitionDataType.getByValue(dataType);
    }

    public void setStatus(int status) {
        this.status = status;
    }

//    /**
//     * format string value to Row.Value
//     *
//     * @return
//     */
//    public Row.Value formatStringValue(String value) {
//        Row.Value rv = Row.Value.createValue(null, value);
//        if (StringUtils.isBlank(value)) {
//            rv.setDataDefinition(this);
//            return rv;
//        }
//        try {
//            switch (dataTypeEnum) {
//                case BOOLEAN:
//                    if (value.equals("是") || value.equals("1")) {
//                        rv.setValue(Integer.valueOf("1"));
//                    } else {
//                        rv.setValue(Integer.valueOf("0"));
//                    }
//                    break;
//                case INT:
//                    rv.setValue(Long.valueOf(value));
//                    break;
//                case STRING:
//                    rv.setValue(value);
//                    break;
//                case DATETIME:
//                    rv.setValue(Long.valueOf(value));
//                    break;
//                case FLOAT:
//                    rv.setValue(Float.valueOf(value));
//                    break;
//                case DOUBLE:
//                    rv.setValue(Double.valueOf(value));
//                    break;
//                case TEXT:
//                    rv.setValue(String.valueOf(value));
//                    break;
//                default:
//                    break;
//            }
//        } catch (NumberFormatException e) {
//            LOG.error("when we do number format operation,we have met an error,the type is [{}], value is [{}]",
//                    this.definitionTypeEnum.toString(), value, e);
//            rv.setValue("-1");
//        }
//        if (rv.getValue() != null) {
//            rv.setDataDefinition(this);
//            return rv;
//        }
//        throw new JsonLogicParseException("data definition is not valid!");
//    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getTagType() {
        return tagType;
    }

    public void setTagType(Integer tagType) {
        this.tagType = tagType;
    }

    public String getDefinitionType() {
        return definitionType;
    }

    public void setDefinitionType(String definitionType) {
        this.definitionType = definitionType;
    }

    public DataDefinition getParent() {
        return parent;
    }

    public void setParent(DataDefinition parent) {
        this.parent = parent;
    }

    public String getChildCommentName() {
        return childCommentName;
    }

    public void setChildCommentName(String childCommentName) {
        this.childCommentName = childCommentName;
    }

    public DataDefinition getChildComment() {
        return childComment;
    }

    public void setChildComment(DataDefinition childComment) {
        this.childComment = childComment;
    }

    public Boolean getQuery() {
        return isQuery;
    }

    public void setQuery(Boolean query) {
        isQuery = query;
    }

    public String getValueColumnName(){
        if(dataTypeEnum == DataDefinitionDataType.DOUBLE || dataTypeEnum == DataDefinitionDataType.FLOAT){
            return "double_value";
        }else if(dataTypeEnum == DataDefinitionDataType.TEXT){
            return "feature";
        }else if(dataTypeEnum == DataDefinitionDataType.BOOLEAN || dataTypeEnum == DataDefinitionDataType.INT || dataTypeEnum == DataDefinitionDataType.DATETIME){
            return "int_value";
        }else if(dataTypeEnum == DataDefinitionDataType.STRING){
            return "str_value";
        }else{
            throw new HappyQueryException("DataDefinitionValue ");
        }
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
