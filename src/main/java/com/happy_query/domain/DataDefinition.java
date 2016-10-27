package com.happy_query.domain;

import com.happy_query.query.cache.DataDefinitionCacheManager;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.NullChecker;
import com.happy_query.util.ReflectionUtil;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by frio on 16/6/14.
 */
public class DataDefinition {
    public static String TABLE_NAME = "statistic_keys";
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

    private String leftColName;

    private Date gmtCreate;

    private Date gmtModified;

    private String isRequired;

    private List<DataOption> dataOptionList;

    public static DataDefinition getDataDefinition(DataSource dataSource, Long id) {
        NullChecker.checkNull(id);
        List<Object> list = Arrays.asList((Object) id);
        try {
            String sql = String.format("select * from %s where id=? and status=0 order by gmt_create desc limit 1", TABLE_NAME);
            List<Map<String, Object>> data = JDBCUtils.executeQuery(dataSource, sql, list);
            return getDataDefinition(data);
        } catch (SQLException e) {
            LOG.error("getDataDefinition failed!", e);
            LOG.error("param id is [{}]", id);
        }
        return new DataDefinitionCacheManager.NullDataDefinition();
    }

    private static DataDefinition getDataDefinition(List<Map<String, Object>> data) {
        if (data == null || data.size() == 0) {
            return new DataDefinitionCacheManager.NullDataDefinition();
        }
        DataDefinition dataDefinition = new DataDefinition();
        ReflectionUtil.cloneMapValueToBean(data.get(0), dataDefinition);
        return dataDefinition;
    }

    public static DataDefinition getDataDefinitionByName(DataSource dataSource, String name) {
        NullChecker.checkNull(name);
        List<Object> list = Arrays.asList((Object) name);
        try {
            String sql = String.format("select * from %s where `key`=? and status=0 order by gmt_create desc limit 1", TABLE_NAME);
            List<Map<String, Object>> data = JDBCUtils.executeQuery(dataSource, sql, list);
            return getDataDefinition(data);
        } catch (SQLException e) {
            LOG.error("getDataDefinitionByName failed!param name is [{}]", name, e);
            throw new HappyQueryException("getDataDefinitionByName failed!param name is " + name, e);
        }
    }

    public static DataDefinition getDataDefinitionByNickName(DataSource dataSource, String name) {
        NullChecker.checkNull(name);
        List<Object> list = new ArrayList();
        list.add(name);
        list.add(name);
        try {
            String sql = String.format("select * from %s where (nick_name=? or name=?) and status=0 order by gmt_create desc limit 1", TABLE_NAME);
            List<Map<String, Object>> data = JDBCUtils.executeQuery(dataSource, sql, list);
            return getDataDefinition(data);
        } catch (SQLException e) {
            LOG.error("getDataDefinitionByNickName failed!param name is [{}]", name, e);
            throw new HappyQueryException("getDataDefinitionByName failed!param name is " + name, e);
        }
    }

    public static void insertDataDefinition(DataSource dataSource, DataDefinition dataDefinition) {
        NullChecker.checkNull(dataDefinition);
        Map<String, Object> map = ReflectionUtil.cloneBeanToMap(dataDefinition);
        try {
            dataDefinition.setId(JDBCUtils.insertToTable(dataSource, TABLE_NAME, map));
        } catch (SQLException e) {
            LOG.error("insert datadefinition failed, datadefinition content is:[{}], t is:[{}]", dataDefinition.toString(), e);
            throw new HappyQueryException("insert failed", e);
        }
    }

    public static int updateDataDefinition(DataSource dataSource, DataDefinition dataDefinition) {
        NullChecker.checkNull(dataDefinition);
        NullChecker.checkNull(dataDefinition.getId());
        Map<String, Object> map = ReflectionUtil.cloneBeanToMap(dataDefinition);
        try {
            return JDBCUtils.executeUpdateById(dataSource, TABLE_NAME, map, "id", dataDefinition.getId());
        } catch (SQLException e) {
            LOG.error("update datadefinition failed, datadefinition content is:[{}], t is:[{}]", dataDefinition.toString(), e);
            throw new HappyQueryException("update failed", e);
        }
    }

    public static void setDataDefinitionTableName(String ddName){
        TABLE_NAME = ddName;
    }

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

    public String getLeftColName() {
        return leftColName;
    }

    public void setLeftColName(String leftColName) {
        this.leftColName = leftColName;
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

    public List<DataOption> getDataOptionList() {
        return dataOptionList;
    }

    public void setDataOptionList(List<DataOption> dataOptionList) {
        this.dataOptionList = dataOptionList;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
