package com.happ_query.test;

import com.happy_query.writer.domain.PrmUserInfo;
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


}
