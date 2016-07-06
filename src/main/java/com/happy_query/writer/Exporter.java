package com.happy_query.writer;

import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.JsonParseDataParam;
import com.happy_query.query.IQuery;

import java.io.File;
import java.util.List;

/**
 * export csv file that contains
 * Created by frio on 16/7/6.
 */
public class Exporter implements IExporter {
    IQuery query;

    public Exporter(IQuery query){
        this.query = query;
    }

    public File queryByJsonLogic(JsonParseDataParam jsonParseDataParam, String tmpDir, List<DataDefinition> definitionIds) {
        query.queryByJsonLogic(jsonParseDataParam);
        return null;
    }
}
