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
import com.opencsv.CSVWriter;
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

    public File queryByJsonLogic(JsonParseDataParam jsonParseDataParam, String tmpDir, String token, List<DataDefinition> definitions) throws IOException {
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
                        String viewStr = "无";
                        if (v != null && v.getValue() != null) {
                            if (dataDefinition.getDefinitionType() == DefinitionType.SELECT) {
                                viewStr = getFromDataOptionList(dataDefinition.getDataOptionList(), v.getValue());
                            } else if (dataDefinition.getDefinitionType() == DefinitionType.DATETIME) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                viewStr = simpleDateFormat.format(new Date(Long.valueOf(v.getValue().toString())));
                            } else if (dataDefinition.getTag()) {
                                viewStr = dataDefinition.getNickName();
                            } else {
                                viewStr = function.render(v, dataDefinition.getId(), "export").toString();
                            }
                            entries[index] = viewStr;
                            index++;
                        }

                    }
                    writer.writeNext(entries);
                }
                if (queryResult.getCount() < PAGE_MAX_SIZE || countNum > 10000) {
                    break;
                }
                jsonParseDataParam.setLimitStart(jsonParseDataParam.getLimitStart() + jsonParseDataParam.getSize());
                countNum++;
            }
        }catch(Exception e){
            LOG.error("when doing export, we have met an unexpected exception", e);
            throw new HappyQueryException(e);
        }finally {
            writer.close();
        }
        return file;
    }

    private String getFromDataOptionList(List<DataOption> dataOptions, Object value) {
        for(DataOption d : dataOptions){
            if(d.getCode().equals(value.toString())){
                return d.getValue();
            }
        }
        return value.toString();
    }
}
