package com.happ_query.test;

import com.happy_query.query.cache.DataDefinitionCacheManager;
import com.happy_query.domain.DataDefinition;
import com.happy_query.domain.DataDefinitionDataType;
import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testTagGroup(){
        List<String> tagsKeys = new ArrayList<>();
        tagsKeys.add("tag1");
        tagsKeys.add("tag2");
        List<String> result = DataDefinitionCacheManager.groupDdTriggered(tagsKeys);
        Assert.assertTrue(result.size() > 0 && result.size() == 1);
        Assert.assertTrue(result.get(0).equals("tag_group"));
    }

    /**
     * 测试创建组标签
     */
    @Test
    public void testCreateGroupDataDefinition(){
        DataDefinition groupTag = new DataDefinition();
        groupTag.setNickName("groupTag");
        groupTag.setDescription("groupTagDescription");
        String computationJson = "[\n" +
                "  \"and\",\n" +
                "  {\n" +
                "    \"attr\": \"dd1\",\n" +
                "    \"operator\":\"contains\",\n" +
                "    \"value\": [\"dd2\",\"dd3\",\"dd4\"]\n" +
                "  },\n" +
                "  {\n" +
                "    \"attr\": \"dd2\",\n" +
                "    \"operator\":\"equals\",\n" +
                "    \"value\": \"abc\"\n" +
                "  }\n" +
                "]\n";
        groupTag.setComputationJson(computationJson);
        List<DataDefinition> childsTag = new ArrayList<>();
        DataDefinition childTag1 = new DataDefinition();
        childTag1.setNickName("childTag1");
        childTag1.setComputationJson(
                "{\"attr\": \"dd5\",\n" +
                        "    \"operator\":\"range\",\n" +
                        "    \"value\": [\"0\", \"10\"]\n" +
                        "  }");
        childTag1.setDescription("childTag1");
        childsTag.add(childTag1);
        DataDefinition childTag2 = new DataDefinition();
        childTag2.setNickName("childTag1");
        childTag2.setComputationJson(
                "{\"attr\": \"dd5\",\n" +
                        "    \"operator\":\"range\",\n" +
                        "    \"value\": [\"10\", \"20\"]\n" +
                        "  }");
        childTag2.setDescription("childTag2");
        childsTag.add(childTag2);
        DataDefinition.insertGroupTagDataDefinition(dataSource, groupTag, childsTag, Constant.DYNAMIC_BIAO_QIAN);
    }

    @Test
    public void testStringRangeOverride(){
        List<DataDefinition> childsTags = new ArrayList<>();
        DataDefinition childTag1 = new DataDefinition();
        childTag1.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"010\", \"060\"]\n" +
                "  }");
        childTag1.setDataType("string");
        childsTags.add(childTag1);

        DataDefinition childTag2 = new DataDefinition();
        childTag2.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"050\", \"080\"]\n" +
                "  }");
        childTag2.setDataType("string");
        childsTags.add(childTag2);

        DataDefinition childTag3 = new DataDefinition();
        childTag3.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"090\", \"095\"]\n" +
                "  }");
        childTag3.setDataType("string");
        childsTags.add(childTag3);

        try{
            DataDefinition.validateRange(childsTags);
        }catch(HappyQueryException e){
            Assert.assertEquals(e.getMessage(), Constant.HAPPY_QUERY_ERROR_RULE_OVERRIDE);
        }
    }

    @Test
    public void testStringRangeNotOverride(){
        List<DataDefinition> childsTags = new ArrayList<>();
        DataDefinition childTag1 = new DataDefinition();
        childTag1.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"010\", \"060\"]\n" +
                "  }");
        childTag1.setDataType("string");
        childsTags.add(childTag1);

        DataDefinition childTag2 = new DataDefinition();
        childTag2.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"070\", \"080\"]\n" +
                "  }");
        childTag2.setDataType("string");
        childsTags.add(childTag2);

        DataDefinition childTag3 = new DataDefinition();
        childTag3.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"090\", \"095\"]\n" +
                "  }");
        childTag3.setDataType("string");
        childsTags.add(childTag3);

        try{
            DataDefinition.validateRange(childsTags);
        }catch(HappyQueryException e){
            Assert.assertEquals(e.getMessage(), Constant.HAPPY_QUERY_ERROR_RULE_OVERRIDE);
        }
    }


    @Test
    public void testIntRangeNotOverride(){
        List<DataDefinition> childsTags = new ArrayList<>();
        DataDefinition childTag1 = new DataDefinition();
        childTag1.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"010\", \"060\"]\n" +
                "  }");
        childTag1.setDataType("string");
        childsTags.add(childTag1);

        DataDefinition childTag2 = new DataDefinition();
        childTag2.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"070\", \"080\"]\n" +
                "  }");
        childTag2.setDataType("string");
        childsTags.add(childTag2);

        DataDefinition childTag3 = new DataDefinition();
        childTag3.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"090\", \"095\"]\n" +
                "  }");
        childTag3.setDataType("string");
        childsTags.add(childTag3);
        DataDefinition.validateRange(childsTags);
    }

    @Test
    public void testIntRangeNumberNotOverride(){
        List<DataDefinition> childsTags = new ArrayList<>();
        DataDefinition childTag1 = new DataDefinition();
        childTag1.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"10\", \"100\"]\n" +
                "  }");
        childTag1.setDataType("int");
        childsTags.add(childTag1);

        DataDefinition childTag2 = new DataDefinition();
        childTag2.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"110\", \"120\"]\n" +
                "  }");
        childTag2.setDataType("int");
        childsTags.add(childTag2);

        DataDefinition childTag3 = new DataDefinition();
        childTag3.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"130\", \"150\"]\n" +
                "  }");
        childTag3.setDataType("int");
        childsTags.add(childTag3);
        DataDefinition.validateRange(childsTags);
    }

    @Test
    public void testIntRangeNumberOverride(){
        List<DataDefinition> childsTags = new ArrayList<>();
        DataDefinition childTag1 = new DataDefinition();
        childTag1.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"10\", \"100\"]\n" +
                "  }");
        childTag1.setDataType("int");
        childsTags.add(childTag1);

        DataDefinition childTag2 = new DataDefinition();
        childTag2.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"110\", \"120\"]\n" +
                "  }");
        childTag2.setDataType("int");
        childsTags.add(childTag2);

        DataDefinition childTag3 = new DataDefinition();
        childTag3.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"10\", \"150\"]\n" +
                "  }");
        childTag3.setDataType("int");
        childsTags.add(childTag3);
        try {
            DataDefinition.validateRange(childsTags);
        }catch(HappyQueryException e){
            Assert.assertEquals(Constant.HAPPY_QUERY_ERROR_RULE_OVERRIDE, e.getMessage());
            return;
        }
        throw new RuntimeException();
    }

    @Test
    public void testDoubleRangeNumberOverride(){
        List<DataDefinition> childsTags = new ArrayList<>();
        DataDefinition childTag1 = new DataDefinition();
        childTag1.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"10\", \"100\"]\n" +
                "  }");
        childTag1.setDataType("double");
        childsTags.add(childTag1);

        DataDefinition childTag2 = new DataDefinition();
        childTag2.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"110\", \"120\"]\n" +
                "  }");
        childTag2.setDataType("double");
        childsTags.add(childTag2);

        DataDefinition childTag3 = new DataDefinition();
        childTag3.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"10\", \"150\"]\n" +
                "  }");
        childTag3.setDataType("double");
        childsTags.add(childTag3);
        try {
            DataDefinition.validateRange(childsTags);
        }catch(HappyQueryException e){
            Assert.assertEquals(Constant.HAPPY_QUERY_ERROR_RULE_OVERRIDE, e.getMessage());
            return;
        }
        throw new RuntimeException();
    }

    @Test
    public void testContainsOverride(){
        List<DataDefinition> childsTags = new ArrayList<>();
        DataDefinition childTag1 = new DataDefinition();
        childTag1.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"contains\",\n" +
                "    \"value\": [\"200\", \"310\"]\n" +
                "  }");
        childTag1.setDataType("string");
        childsTags.add(childTag1);

        DataDefinition childTag2 = new DataDefinition();
        childTag2.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"contains\",\n" +
                "    \"value\": [\"310\", \"320\"]\n" +
                "  }");
        childTag2.setDataType("string");
        childsTags.add(childTag2);

        DataDefinition childTag3 = new DataDefinition();
        childTag3.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"10\", \"150\"]\n" +
                "  }");
        childTag3.setDataType("string");
        childsTags.add(childTag3);
        try {
            DataDefinition.validateRange(childsTags);
        }catch(HappyQueryException e){
            Assert.assertEquals(Constant.HAPPY_QUERY_ERROR_RULE_OVERRIDE, e.getMessage());
            return;
        }
        throw new RuntimeException();
    }

    @Test
    public void testContainsOverride1(){
        List<DataDefinition> childsTags = new ArrayList<>();
        DataDefinition childTag1 = new DataDefinition();
        childTag1.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"contains\",\n" +
                "    \"value\": \"900\"\n" +
                "  }");
        childTag1.setDataType("string");
        childsTags.add(childTag1);

        DataDefinition childTag2 = new DataDefinition();
        childTag2.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"contains\",\n" +
                "    \"value\": [\"330\", \"320\"]\n" +
                "  }");
        childTag2.setDataType("string");
        childsTags.add(childTag2);

        DataDefinition childTag3 = new DataDefinition();
        childTag3.setComputationJson("{\"attr\": \"dd4\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"400\", \"500\"]\n" +
                "  }");
        childTag3.setDataType("string");
        childsTags.add(childTag3);
        try {
            DataDefinition.validateRange(childsTags);
        }catch(HappyQueryException e){
            Assert.assertEquals(Constant.HAPPY_QUERY_ERROR_RULE_OVERRIDE, e.getMessage());
            return;
        }
        throw new RuntimeException();
    }

    @Test
    public void testDescribeFunction(){
        System.out.println(DataDefinition.describeExpression("(&&  (>= dd5 10) (<= dd5 2147483647) (== dd2 \"1\") (== dd3 \"1\") (== dd4 \"1\") (== dd2 \"abc\"))"));
    }
}
