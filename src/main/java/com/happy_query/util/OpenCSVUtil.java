package com.happy_query.util;

import com.happy_query.query.domain.Row;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * Created by frio on 16/6/17.
 */
public class OpenCSVUtil {

    /**
     * Read all from csv file
     * format key and value
     *  -> data_definition_value Row
     * @param reader
     * @return
     */
    public static List<Row> readAll(Reader reader, int skipNum, char quoted, char seperator){
        CSVReader csvReader = new CSVReader(reader,seperator, quoted, skipNum);


        return null;
    }

    public static List<Row> readAllDefaultTemplate(Reader reader){
        CSVReader csvReader = new CSVReader(reader);
        try {
            List<String[]> rows = csvReader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
