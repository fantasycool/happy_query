package com.happ_query.test;

import org.junit.Test;

import java.util.HashMap;
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
        datas.put("dd2", "dd2_value");
        datas.put("weight", 75);
        datas.put("height", 1.75);
        System.out.println(writer.insertRecord(datas, source, userKey, empName));
    }

    @Test
    public void testUpdateWeight(){

    }


}
