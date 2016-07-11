package com.happy_query.util;

import com.happy_query.parser.domain.DataDefinition;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.Reader;

/**
 * extend freemarker Template to put into CacheManager
 * Created by frio on 16/6/16.
 */
public class DataDefinitionTemplate extends Template {
    private DataDefinition dataDefinition;

    public DataDefinitionTemplate(DataDefinition dataDefinition, String name, Reader reader, Configuration cfg, String encoding) throws IOException {
        super(name, reader, cfg, encoding);
        this.dataDefinition = dataDefinition;
    }

    public DataDefinitionTemplate(DataDefinition dataDefinition, String name, Reader reader, Configuration cfg) throws IOException {
        super(name, reader, cfg);
        this.dataDefinition = dataDefinition;
    }

    public DataDefinitionTemplate(DataDefinition dataDefinition, String name, Reader reader) throws IOException {
        super(name, reader);
        this.dataDefinition = dataDefinition;
    }

    public int hashCode() {
        return Integer.valueOf(dataDefinition.getId().toString());
    }

    public boolean equals(Object o){
        if(o instanceof DataDefinitionTemplate){
            if(this.dataDefinition.getId() == ((DataDefinitionTemplate) o).dataDefinition.getId()){
                return true;
            }
        }
        return false;
    }
}
