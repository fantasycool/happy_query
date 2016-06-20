package com.happy_query.util;

import com.happy_query.parser.dao.DataDefinitionDao;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.query.domain.Row;
import com.happy_query.writer.HappyWriterException;
import com.happy_query.writer.domain.InsertResult;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Created by frio on 16/6/17.
 */
public class OpenCSVUtil {
    private static Logger LOG = LoggerFactory.getLogger(OpenCSVUtil.class);

    /**
     * Read all from csv file
     * format key and value
     * -> data_definition_value Row
     *
     * @param reader
     * @return
     */
    public static List<Row> readAll(Reader reader, int skipNum, char quoted, char seperator) {
        CSVReader csvReader = new CSVReader(reader, seperator, quoted, skipNum);
        return null;
    }

    /**
     * use default config to read from import file
     *
     * @param dataSource
     * @param reader
     * @return
     */
    public static InsertResult readAllDefaultTemplate(Reader reader, DataSource dataSource) {
        CSVReader csvReader = new CSVReader(reader);
        InsertResult insertResult = new InsertResult();
        try {
            String categoryType = null;
            Map<Integer, DataDefinition> definitions = new LinkedHashMap<Integer, DataDefinition>();
            List<String[]> rows = csvReader.readAll();
            List<Row> resultRows = new ArrayList<Row>();
            for (int i = 0; i < rows.size(); i++) {
                if (i > 0) {
                    if (i > 1 && rows.get(i).length <= 1) {
                        LOG.info("import now we have come to the end");
                        break;
                    }
                    /**
                     * get category type data
                     */
                    if (i == 1) {
                        categoryType = rows.get(i)[0];
                        insertResult.setCategoryType(categoryType);
                        continue;
                    }
                    /**
                     * get definitions
                     */
                    if (i == 2) {
                        String[] headers = rows.get(i);
                        for (int j = 0; j < headers.length; j++) {
                            if (j > 0) {
                                definitions.put(j, DataDefinitionDao.getDataDefinitionByName(dataSource, getDefinitionName(headers[j])));
                            }
                        }
                        continue;
                    }
                    /**
                     * fill data
                     */
                    String[] flatRow = rows.get(i);
                    Row row = new Row();
                    Long leftId = Long.valueOf(flatRow[0].trim());
                    Map<DataDefinition, Row.Value> dataMap = new LinkedHashMap<DataDefinition,  Row.Value>();
                    for (int y = 0; y < flatRow.length; y++) {
                        if(y > 0){
                            DataDefinition dataDefinition = definitions.get(y);
                            Row.Value rv = dataDefinition.formatStringValue(flatRow[y]);
                            dataMap.put(dataDefinition, rv);
                        }
                    }
                    row.setData(dataMap);
                    row.setLeftId(leftId);
                    resultRows.add(row);
                }
            }
            insertResult.setRows(resultRows);
            return insertResult;
        } catch (IOException e) {
            LOG.error("readAllDefaultTemplate unexpected exception", e);
            throw new HappyWriterException("unexpected exception", e);
        }
    }

    private static String getDefinitionName(String header) {
        int index = header.trim().indexOf("[");
        return header.substring(0, index);
    }
}
