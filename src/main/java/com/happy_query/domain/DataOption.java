package com.happy_query.domain;

import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.JDBCUtils;
import com.happy_query.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by frio on 16/10/27.
 */
public class DataOption {
    static Logger LOG = LoggerFactory.getLogger(DataOption.class);

    private Long id;
    private Integer type;
    private Long dId;
    private Long parentId;
    private String optionValue;
    private String option;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getdId() {
        return dId;
    }

    public void setdId(Long dId) {
        this.dId = dId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    /**
     * 获取选项列表
     * @param dataSource
     * @param ddId
     * @return
     */
    public static List<DataOption> queryDataOptionsByDDId(DataSource dataSource, Long ddId){
        if(null == dataSource || null == ddId){
            throw new IllegalArgumentException();
        }
        List<Object> params =  new ArrayList<>();
        params.add(ddId);
        try {
            List<Map<String, Object>> list = JDBCUtils.executeQuery(dataSource, "select * from " + Constant.PRM_DATA_OPTIONS + " where d_id=?", params);
            List<DataOption> resultList = new ArrayList<>();
            for(Map<String, Object> m : list){
                DataOption dataOption = new DataOption();
                ReflectionUtil.cloneMapValueToBean(m, dataOption);
                resultList.add(dataOption);
            }
            return resultList;
        } catch (SQLException e) {
            LOG.error("get data options failed", e);
            throw new HappyQueryException("get data options failed,ddId:" + ddId, e);
        }
    }
}
