package com.happy_query.writer;

import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.util.Function;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * export csv file that contains
 * Created by frio on 16/7/6.
 */
public class Exporter implements IExporter {
    @Override
    public File export(JsonParseDataParam jsonParseDataParam, String tmpDir, String token, List<DataDefinition> definitionIds, Function function) throws IOException {
        return null;
    }
//    IQuery query;
//    Function function;
//    final static int PAGE_MAX_SIZE = 100;
//    static Logger LOG = LoggerFactory.getLogger(Exporter.class);
//
//    public Exporter(IQuery query, Function function){
//        this.query = query;
//        this.function = function;
//    }
//
//    public File export(JsonParseDataParam jsonParseDataParam, String tmpDir, String token, List<DataDefinition> definitions, Function function) throws IOException {
//        String fileName = tmpDir + File.separator + System.currentTimeMillis() + "_" + token + ".csv";
//        File file = new File(fileName);
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            throw new RuntimeException("cannot create file, fileName is" + fileName, e);
//        }
//        jsonParseDataParam.setLimitStart(0);
//        jsonParseDataParam.setSize(PAGE_MAX_SIZE);
//        int countNum = 0;
//        CSVWriter writer = new CSVWriter(new FileWriter(fileName), '\t');
//        /**
//         * write header first
//         */
//        String[] headers = new String[definitions.size()];
//        for (int i = 0; i < definitions.size(); i++) {
//            headers[i] = StringUtils.isBlank(definitions.get(i).getNickName()) ?
//                    definitions.get(i).getKey() : definitions.get(i).getNickName();
//        }
//        writer.writeNext(headers);
//        /**
//         * ok, let's write data to file
//         */
//        try {
//            while (true) {
//                QueryResult queryResult = query.queryByJsonLogic(jsonParseDataParam);
//                List<Row> rows = queryResult.getRows();
//                for (Row r : rows) {
//                    Map<Long, Row.Value> m = r.getFlatMapData();
//                    String[] entries = new String[definitions.size()];
//                    int index = 0;
//                    for (DataDefinition dataDefinition : definitions) {
//                        long id = dataDefinition.getId();
//                        Row.Value v = m.get(id);
//                        String viewStr;
//                        if (v != null && v.getValue() != null) {
//                            try {
//                                viewStr = Transformer.dressUp(dataDefinition, v.getValue(), function);
//                                if(StringUtils.isBlank(viewStr)){
//                                    viewStr = "无";
//                                }
//                            } catch (Exception e) {
//                                LOG.error("data convert failed!", e);
//                                viewStr = "-";
//                            }
//                            entries[index] = viewStr;
//                        }else{
//                            entries[index] = "无";
//                        }
//                        index++;
//                    }
//                    writer.writeNext(entries);
//                }
//                if (queryResult.getRows().size() < PAGE_MAX_SIZE || countNum > 10000) {
//                    break;
//                }
//                jsonParseDataParam.setLimitStart(jsonParseDataParam.getLimitStart() + jsonParseDataParam.getSize());
//                countNum++;
//            }
//        } catch (Exception e) {
//            LOG.error("when doing export, we have met an unexpected exception", e);
//            throw new HappyQueryException(e);
//        } finally {
//            writer.close();
//        }
//        return file;
//    }
}
