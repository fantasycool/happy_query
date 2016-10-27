package com.happ_query.test;

import com.happy_query.query.cache.DataDefinitionCacheManager;
import com.happy_query.parser.domain.DataDefinition;
import com.happy_query.parser.domain.DataDefinitionDataType;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by frio on 16/10/26.
 */
public class DataDefinitionTest extends BaseTest {
    /**
     * 測試根據key獲取指標
     */
    @Test
    public void testGetDataDefinitionByKey(){
        DataDefinition dataDefinition = DataDefinitionCacheManager.getDataDefinition("dd2");
        Assert.assertNotNull(dataDefinition);
        Assert.assertEquals("dd2", dataDefinition.getKey());
        Assert.assertTrue(dataDefinition.getQuery());
        Assert.assertEquals("dd2_comment", dataDefinition.getChildComment().getKey());
        Assert.assertEquals(DataDefinitionDataType.STRING, dataDefinition.getDataTypeEnum());
        Assert.assertTrue(dataDefinition.getLeftData());
    }

}
