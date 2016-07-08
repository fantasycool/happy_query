package com.happy_query.writer;

import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.DataOption;
import com.happy_query.parser.domain.DefinitionType;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.IQuery;
import com.happy_query.query.domain.QueryResult;
import com.happy_query.query.domain.Row;
import com.happy_query.util.Function;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.Transformer;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * export csv file that contains
 * Created by frio on 16/7/6.
 */
public class Exporter implements IExporter {
    IQuery query;
    Function function;
    final static int PAGE_MAX_SIZE = 100;
    static Logger LOG = LoggerFactory.getLogger(Exporter.class);

    public Exporter(IQuery query, Function function){
        this.query = query;
        this.function = function;
    }

    public File export(JsonParseDataParam jsonParseDataParam, String tmpDir, String token, List<DataDefinition> definitions) throws IOException {
        String fileName = tmpDir + File.separator + System.currentTimeMillis() + "_" + token + ".csv";
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("cannot create file, fileName is" + fileName, e);
        }
        jsonParseDataParam.setLimitStart(0);
        jsonParseDataParam.setSize(PAGE_MAX_SIZE);
        int countNum = 0;
        CSVWriter writer = new CSVWriter(new FileWriter(fileName), '\t');
        /**
         * write header first
         */
        String[] headers = new String[definitions.size()];
        for (int i = 0; i < definitions.size(); i++) {
            headers[i] = StringUtils.isBlank(definitions.get(i).getNickName()) ?
                    definitions.get(i).getName() : definitions.get(i).getNickName();
        }
        writer.writeNext(headers);
        /**
         * ok, let's write data to file
         */
        try {
            while (true) {
                QueryResult queryResult = query.queryByJsonLogic(jsonParseDataParam);
                List<Row> rows = queryResult.getRows();
                for (Row r : rows) {
                    Map<Long, Row.Value> m = r.getFlatMapData();
                    String[] entries = new String[definitions.size()];
                    int index = 0;
                    for (DataDefinition dataDefinition : definitions) {
                        long id = dataDefinition.getId();
                        Row.Value v = m.get(id);
                        String viewStr;
                        if (v != null && v.getValue() != null) {
                            try {
                                viewStr = Transformer.dressUp(dataDefinition, v, new Function());
                            } catch (Exception e) {
                                LOG.error("data convert failed!", e);
                                viewStr = "-";
                            }
                            entries[index] = viewStr;
                            index++;
                        }

                    }
                    writer.writeNext(entries);
                }
                if (queryResult.getRows().size() < PAGE_MAX_SIZE || countNum > 10000) {
                    break;
                }
                jsonParseDataParam.setLimitStart(jsonParseDataParam.getLimitStart() + jsonParseDataParam.getSize());
                countNum++;
            }
        } catch (Exception e) {
            LOG.error("when doing export, we have met an unexpected exception", e);
            throw new HappyQueryException(e);
        } finally {
            writer.close();
        }
        return file;
    }
}
