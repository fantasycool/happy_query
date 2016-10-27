package com.happ_query.test;

import com.alibaba.fastjson.JSON;
import com.happy_query.domain.PrmUserInfo;
import junit.framework.Assert;
import org.javatuples.Pair;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by frio on 16/10/26.
 */
public class ReaderWriterTest extends BaseTest {
    String source = "zsty";
    String userKey = "1";
    String empName = "testUser";

    /**
     * 1.動態指標更新
     * 2.BMI指標的自動計算
     * 3.操作人數據更新
     */
    @Test
    public void testInsertData(){
        Map<String, Object> datas = new HashMap<>();
        datas.put("dd2", "dd2");
        datas.put("weight", 75);
        datas.put("height", 1.75);
        System.out.println(writer.insertRecord(datas, source, userKey, empName));
    }

    /**
     * 更新weight的值的時候會同步更新BMI的值
     */
    @Test
    public void testUpdateWeight(){
        Map<String, Object> datas = new HashMap<>();
        PrmUserInfo prmUserInfo = PrmUserInfo.getPrmUserInfoBySourceAndUserKey(dataSource, "1", "zsty");
        //更新weight的值,查看BMI和weight的指標是否會同時變化
        datas.put("weight", 80);
        datas.put("dd2", "dd2");
        writer.updateRecord(datas, prmUserInfo.getId(), empName);
        //同時更新寬表和縱向表的值
        datas.put("dd2", "dd2_value2");
        datas.put("height", 1.80);
        writer.updateRecord(datas, prmUserInfo.getId(), empName);
    }

    @Test
    public void testQueryById() throws SQLException {
        Connection connection = dataSource.getConnection();
        List<String> keys = new ArrayList<>();
        keys.add("dd2");
        keys.add("dd2_comment");
        keys.add("BMI");
        Map<String, Object> map = query.getPrmUserInfo(28l, keys, connection);
        Assert.assertNotNull(map.get("dd2"));
        Assert.assertNotNull(map.get("dd2_comment"));
        Assert.assertNotNull(map.get("BMI"));
        Assert.assertNull(map.get("weight"));
    }

    @Test
    public void testQueryByJson() {
        String json = "[\n" +
                "  \"and\",\n" +
                "  {\n" +
                "    \"attr\": \"weight\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"10\", \"100\"]\n" +
                "  },\n" +
                "  {\n" +
                "    \"attr\": \"height\",\n" +
                "    \"operator\":\"range\",\n" +
                "    \"value\": [\"0\", \"20\"]\n" +
                "  }" +
                "]\n";
        Pair<Integer, List<Map<String, Object>>> pair = query.queryPrmUserInfosByJson(json, 0, 10);
        System.out.println(pair.getValue0());
        System.out.println(JSON.toJSONString(pair.getValue1()));
        Assert.assertTrue(pair.getValue0() > 0);
        Assert.assertTrue(pair.getValue1() != null);
        Assert.assertTrue(pair.getValue1().size() > 0);
    }

}
