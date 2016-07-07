package com.happy_query.writer;

import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.IQuery;
import com.happy_query.query.domain.QueryResult;

import java.io.File;
import java.util.List;

/**
 * export csv file that contains
 * Created by frio on 16/7/6.
 */
public class Exporter implements IExporter {
    IQuery query;
    final static int PAGE_MAX_SIZE = 100;


    public Exporter(IQuery query){
        this.query = query;
    }

    public File queryByJsonLogic(JsonParseDataParam jsonParseDataParam, String tmpDir, String token, List<DataDefinition> definitions) {
        String fileName = tmpDir + File.separator + System.currentTimeMillis() + "_" + token + ".csv";
        File file = new File(fileName);
        jsonParseDataParam.setLimitStart(0);
        jsonParseDataParam.setSize(PAGE_MAX_SIZE);
        while(true){
            QueryResult queryResult = query.queryByJsonLogic(jsonParseDataParam);
            if(queryResult.getCount() < PAGE_MAX_SIZE){
                break;
            }
        }
        return file;
    }
}
