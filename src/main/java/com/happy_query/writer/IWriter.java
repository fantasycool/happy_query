package com.happy_query.writer;

import com.happy_query.writer.domain.ImportParam;
import com.happy_query.writer.domain.InsertResult;
import com.happy_query.writer.domain.Record;

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
    void writeRecord(InsertResult insertResult);

    /**
     * update data
     *
     * @param insertResult
     */
    void updateRecord(InsertResult insertResult);

    /**
     * delete record by leftId
     *
     * @param leftId
     * @param category
     * @param leftIdColumnName
     */
    void deleteRecord(long leftId, String category, String leftIdColumnName);
}
