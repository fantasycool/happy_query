package com.happy_query.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.happy_query.parser.JsonSqlParser;
import com.happy_query.query.cache.DataDefinitionCacheManager;
import com.happy_query.util.*;
import com.jkys.moye.DynamicVariable;
import com.jkys.moye.MoyeParser;
import com.jkys.moye.MoyeParserImpl;
import com.jkys.moye.Word;
import org.apache.commons.lang3.StringUtils;
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
    private String computationJson;

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
            String sql = String.format("select * from %s where id=? and status=0 order by gmt_create desc limit 1", Constant.TABLE_NAME);
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
            String sql = String.format("select * from %s where `key`=? and status=0 order by gmt_create desc limit 1", Constant.TABLE_NAME);
            List<Map<String, Object>> data = JDBCUtils.executeQuery(dataSource, sql, list);
            return getDataDefinition(data);
        } catch (SQLException e) {
            LOG.error("getDataDefinitionByName failed!param name is [{}]", name, e);
            throw new HappyQueryException("getDataDefinitionByName failed!param name is " + name, e);
        }
    }

    public static List<DataDefinition> queryGroupDataDefinitions(DataSource dataSource){
        NullChecker.checkNull(dataSource);
        List<Object> params = new ArrayList<>();
        try {
            List<Map<String, Object>> datas = JDBCUtils.executeQuery(dataSource, "select * from " + Constant.TABLE_NAME + " where tag_type=3 and status=0", params);
            List<DataDefinition> result = new ArrayList<>();
            for(Map<String, Object> data : datas){
                result.add(DataDefinitionCacheManager.getDataDefinition(data.get("key").toString()));
            }
            return result;
        } catch (SQLException e) {
            LOG.error("queryGroupDataDefinitions failed", e);
            throw new HappyQueryException(e);
        }
    }

    public static DataDefinition getDataDefinitionByNickName(DataSource dataSource, String name) {
        NullChecker.checkNull(name);
        List<Object> list = new ArrayList();
        list.add(name);
        list.add(name);
        try {
            String sql = String.format("select * from %s where (nick_name=? or key=?) and status=0 order by gmt_create desc limit 1", Constant.TABLE_NAME);
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
            dataDefinition.setId(JDBCUtils.insertToTable(dataSource, Constant.TABLE_NAME, map));
        } catch (SQLException e) {
            LOG.error("insert datadefinition failed, datadefinition content is:[{}], t is:[{}]", dataDefinition.toString(), e);
            throw new HappyQueryException("insert failed", e);
        }
    }

    public static int updateDataDefinition(DataSource dataSource, DataDefinition dataDefinition) {
        NullChecker.checkNull(dataDefinition, dataDefinition.getId());
        Map<String, Object> map = ReflectionUtil.cloneBeanToMap(dataDefinition);
        try {
            //清空掉缓存
            DataDefinitionCacheManager.delByKey(dataDefinition.getKey());
            return JDBCUtils.executeUpdateById(dataSource, Constant.TABLE_NAME, map, "id", dataDefinition.getId());
        } catch (SQLException e) {
            LOG.error("update datadefinition failed, datadefinition content is:[{}], t is:[{}]", dataDefinition.toString(), e);
            throw new HappyQueryException("update failed", e);
        }
    }

    /**
     * 新增标签指标的时候调用,根据标签类型
     *  1. 系统标签. 多个指标的组合生成,这些多个指标都和此标签为映射关系
     *  2. 动态标签. 多个指标的组合生成,这些多个指标都和此标签为映射关系
     * @param dataSource
     * @param dataDefinition
     */
    public static void insertTagDataDefinition(DataSource dataSource, DataDefinition dataDefinition){
        if(dataDefinition.getType() != 1){
            throw new HappyQueryException("要创建的指标不为标签指标");
        }
        if(StringUtils.isNoneBlank(dataDefinition.getComputationRule())){
            MoyeParser moyeParser = new MoyeParserImpl();
            List<Word> words = moyeParser.parseExpression(dataDefinition.getComputationRule());
            for(Word w : words){
                if(w instanceof DynamicVariable){
                    KeyRelation.insertKeyRelation(dataDefinition.getKey(), w.getName(), dataSource);
                }
            }
        }
        insertDataDefinition(dataSource, dataDefinition);
    }

    /**
     * 创建组标签,组标签的子标签, 组标签子标签连带关系
     * @param dataSource
     * @param groupTag nick_name, description, computationJson
     * @param childsTag nick_name, computationJson, description
     * @param tagType 1:系统标签;2:动态标签
     * @return 要进行异步任务打标的子标签列表
     */
    public static List<DataDefinition> insertGroupTagDataDefinition(DataSource dataSource, DataDefinition groupTag,
                                                                    List<DataDefinition> childsTag, int tagType){
        NullChecker.checkNull(groupTag, childsTag, groupTag.getNickName(), groupTag.getDescription(), groupTag.getComputationJson());
        for(DataDefinition dataDefinition : childsTag){
            NullChecker.checkNull(dataDefinition.getNickName(), dataDefinition.getComputationJson(), dataDefinition.getDescription());
        }
        List<DataDefinition> result = new ArrayList<>();
        //create group tag
        JsonSqlParser jsonSqlParser = new JsonSqlParser();
        if(StringUtils.isNoneBlank(groupTag.getComputationJson())){
            groupTag.setComputationRule(jsonSqlParser.convertJsonToLispExpression(groupTag.getComputationJson()));
        }
        groupTag.setType(Constant.TAG_TYPE);
        groupTag.setTagType(Constant.GROUP_BIAO_QIAN);
        groupTag.setKey(String.valueOf(System.currentTimeMillis()));
        groupTag.setDefinitionType("input");
        groupTag.setDataType("int");
        insertDataDefinition(dataSource, groupTag);
        //create childs tags
        int i = 0;
        for(DataDefinition dataDefinition : childsTag){
            dataDefinition.setKey(String.valueOf(System.currentTimeMillis()) + i);
            dataDefinition.setComputationRule(jsonSqlParser.convertJsonToLispExpression(mergeJson(groupTag.getComputationJson(), dataDefinition.getComputationJson())));
            dataDefinition.setType(Constant.TAG_TYPE);
            dataDefinition.setTagType(tagType);
            dataDefinition.setDefinitionType("input");
            dataDefinition.setDataType("int");
            insertDataDefinition(dataSource, dataDefinition);

            PrmTagKeyRelation prmTagKeyRelation = new PrmTagKeyRelation();
            prmTagKeyRelation.setGroupKey(groupTag.getKey());
            prmTagKeyRelation.setSubKey(dataDefinition.getKey());
            PrmTagKeyRelation.insert(dataSource, prmTagKeyRelation);
            result.add(dataDefinition);
            i ++;
        }
        //end
        return result;
    }

    /**
     * 合并组标签和子标签的json生成一个合并结果的json
     * @param computationJson
     * @param computationJson1
     * @return
     */
    private static String mergeJson(String computationJson, String computationJson1) {
        JSONArray groupJSONArray = (JSONArray)JSON.parse(computationJson);
        JSONObject childJsonObject = (JSONObject)JSON.parse(computationJson1);
        List<Object> params = new ArrayList<>();
        String connector = "and";
        params.add(connector);
        fillJsonArrayList(groupJSONArray, params);
        params.add(childJsonObject);
        JSONArray jsonArray = new JSONArray(params);
        return jsonArray.toJSONString();
    }

    private static void fillJsonArrayList(JSONArray groupJSONArray, List<Object> params) {
        for(Object object : groupJSONArray){
            if(!(object instanceof String)){
                params.add(object);
            }
        }
    }


    public static void setDataDefinitionTableName(String ddName){
        Constant.TABLE_NAME = ddName;
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

    public String getComputationJson() {
        return computationJson;
    }

    public void setComputationJson(String computationJson) {
        this.computationJson = computationJson;
    }
}
