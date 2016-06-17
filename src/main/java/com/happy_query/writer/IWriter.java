package com.happy_query.writer;

import com.happy_query.writer.domain.ImportParam;
import com.happy_query.writer.domain.Record;

/**
 * Created by frio on 16/6/16.
 */
public interface IWriter {
    /**
     * import data to happy_query data_definition_value_[xxx] from csv file
     *
     * @param importParam
     */
    void importDataByExcel(ImportParam importParam);

    /**
     * import data to happy_query data_definition_value_[xxx] from List datas
     * @param importParam
     */
    void batchInsertData(ImportParam importParam);


    /**
     * batch update data_definition_value
     * @param importParam
     */
    void batchUpdateData(ImportParam importParam);

    /**
     * @param record
     */
    void writeRecord(Record record);
}
