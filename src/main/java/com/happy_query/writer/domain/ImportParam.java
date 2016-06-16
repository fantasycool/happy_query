package com.happy_query.writer.domain;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * param class for import operation
 * Created by frio on 16/6/16.
 */
public class ImportParam {
    private String leftTableName;
    private String leftPrimaryId;
    private String rightTableName;
    private File importFile;
    private List<Map<Long, Object>> datas;
    private int group;

    /**
     * if we already have the left table,
     * so we import data use left table's primary id as data_definition_value's left_id to insert data
     * @param leftTableName
     * @param leftPrimaryId
     * @param rightTableName
     * @param importFile
     */
    public ImportParam(String leftTableName, String leftPrimaryId, String rightTableName, File importFile, int group){
        this.leftTableName = leftTableName;
        this.leftPrimaryId = leftPrimaryId;
        this.rightTableName = rightTableName;
        this.importFile = importFile;
        this.group = group;
    }

    /**
     * if we don't have left table,we import data use global sequence table to get sequence id as data_defintion_value
     * table's left_id column value
     * @param rightTableName
     * @param importFile
     */
    public ImportParam(String rightTableName, File importFile, int group){
        this.rightTableName = rightTableName;
        this.importFile = importFile;
        this.group = group;
    }

    /**
     * if we already have the left table,
     * so we import data use left table's primary id as data_definition_value's left_id to insert data
     * @param leftTableName
     * @param leftPrimaryId
     * @param rightTableName
     * @param datas
     */
    public ImportParam(String leftTableName, String leftPrimaryId, String rightTableName, List<Map<Long, Object>> datas, int group){
        this.leftTableName = leftTableName;
        this.leftPrimaryId = leftPrimaryId;
        this.rightTableName = rightTableName;
        this.datas = datas;
        this.group = group;
    }

    /**
     * if we don't have left table,we import data use global sequence table to get sequence id as data_defintion_value
     * table's left_id column value
     * @param rightTableName
     * @param datas
     */
    public ImportParam(String rightTableName, List<Map<Long, Object>> datas, int group){
        this.rightTableName = rightTableName;
        this.datas = datas;
        this.group = group;
    }

    public String getLeftTableName() {
        return leftTableName;
    }

    public void setLeftTableName(String leftTableName) {
        this.leftTableName = leftTableName;
    }

    public String getLeftPrimaryId() {
        return leftPrimaryId;
    }

    public void setLeftPrimaryId(String leftPrimaryId) {
        this.leftPrimaryId = leftPrimaryId;
    }

    public String getRightTableName() {
        return rightTableName;
    }

    public void setRightTableName(String rightTableName) {
        this.rightTableName = rightTableName;
    }

    public File getImportFile() {
        return importFile;
    }

    public void setImportFile(File importFile) {
        this.importFile = importFile;
    }

    public List<Map<Long, Object>> getDatas() {
        return datas;
    }

    public void setDatas(List<Map<Long, Object>> datas) {
        this.datas = datas;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }
}
