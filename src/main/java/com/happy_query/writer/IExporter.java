package com.happy_query.writer;

import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.JsonParseDataParam;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by frio on 16/7/6.
 */
public interface IExporter {
    /**
     * export data to File
     * @param jsonParseDataParam query condition object like IQuery use
     * @param tmpDir export tmp file dir
     * @param definitionIds definitions that need to be exported
     * @return
     */
    File export(JsonParseDataParam jsonParseDataParam, String tmpDir, String token, List<DataDefinition> definitionIds) throws IOException;
}
