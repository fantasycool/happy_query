package com.happy_query.domain;

import com.happy_query.util.HappyQueryException;

/**
 * Created by frio on 16/10/18.
 */
public class DataDefinitionValue {
    private Long id;
    private Long prmId;
    private Long ddRefId;
    private Long intValue;
    private String strValue;
    private Double doubleValue;
    private String feature;
    private Integer status;
    private String empName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrmId() {
        return prmId;
    }

    public void setPrmId(Long prmId) {
        this.prmId = prmId;
    }

    public Long getDdRefId() {
        return ddRefId;
    }

    public void setDdRefId(Long ddRefId) {
        this.ddRefId = ddRefId;
    }

    public Long getIntValue() {
        return intValue;
    }

    public void setIntValue(Long intValue) {
        this.intValue = intValue;
    }


    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Object getValue(DataDefinitionDataType dataDefinitionDataType) {
        if(dataDefinitionDataType == DataDefinitionDataType.STRING){
            return this.getStrValue();
        }else if(dataDefinitionDataType == DataDefinitionDataType.TEXT){
            return this.getFeature();
        }else if(dataDefinitionDataType == DataDefinitionDataType.DOUBLE){
            return this.getDoubleValue();
        }else if(dataDefinitionDataType == DataDefinitionDataType.INT){
            return this.getIntValue();
        }
        return null;
    }

    public String getValueColumn() {
        if(this.getDoubleValue() != null){
            return "double_value";
        }else if(this.getFeature() != null){
            return "feature";
        }else if(this.getIntValue() != null){
            return "int_value";
        }else if(this.getStrValue() != null){
            return "str_value";
        }else{
            throw new HappyQueryException("DataDefinitionValue ");
        }
    }
}
