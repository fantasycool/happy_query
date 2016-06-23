package com.happy_query.writer;

import com.happy_query.writer.domain.ImportParam;
import com.happy_query.writer.domain.InsertResult;
import com.happy_query.writer.domain.Record;

import java.util.Map;

/**
 * Created by frio on 16/6/16.
 */
public interface IWriter {
    /**
     * import data to happy_query from csv file
     *
     * @param importParam
     */
    void importDataByCSV(ImportParam importParam);

    /**
     * write data to happy_query
     *
     * @param insertResult
     */
    Long writeRecord(InsertResult insertResult);

    /**
     * update data
     *
     * @param insertResult
     */
    void updateRecord(InsertResult insertResult);

    /**
     * update data
     * @param leftId
     * @param category
     */
    void updateRecord(long leftId, String category, Map<Long, Object> update);

    /**
     * delete record by leftId
     *
     * @param leftId
     * @param category
     * @param leftIdColumnName
     */
    void deleteRecord(long leftId, String category);
}
