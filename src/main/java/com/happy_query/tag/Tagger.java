package com.happy_query.tag;

import com.happy_query.domain.DataDefinition;
import com.happy_query.query.IQuery;
import com.happy_query.query.Query;
import com.happy_query.query.cache.DataDefinitionCacheManager;
import com.happy_query.util.Constant;
import com.happy_query.util.HappyQueryException;
import com.happy_query.util.NullChecker;
import com.happy_query.writer.IWriter;
import com.happy_query.writer.Writer;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by frio on 16/11/3.
 */
public class Tagger implements ITagger {
    static Logger LOG = LoggerFactory.getLogger(Tagger.class);
    private static ThreadPoolExecutor threadPoolExecutor
            = new ThreadPoolExecutor(2, 2, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100));

    @Override
    public void tagDatas(String tagKey, String queryExpression) {
        NullChecker.checkNull(tagKey, queryExpression);
        TaggerTask taggerTask = new TaggerTask(tagKey, queryExpression);
        try {
            threadPoolExecutor.execute(taggerTask);
        }catch(RejectedExecutionException e){
            throw new HappyQueryException(String.format("tagKey:%s; queryExpression:%s task execute failed, waiting task is full", tagKey, queryExpression), e);
        }
    }

    /**
     * 打标任务Worker
     */
    public static class TaggerTask implements Runnable{
        //查询条件表达式
        private String expression;
        private String tagKey;
        private final static int size = 50;

        public TaggerTask(String tagKey, String expression){
            this.tagKey = tagKey;
            this.expression = expression;
        }

        @Override
        public void run() {
            IQuery query = new Query(DataDefinitionCacheManager.dataSource);
            IWriter writer = new Writer(DataDefinitionCacheManager.dataSource);
            //start task status
            DataDefinition.updateTaskProgressStatus(DataDefinitionCacheManager.dataSource, tagKey, Constant.TAG_TASK_STATUS_STARTED);
            int start = 0;
            try {
                while (true) {
                    Pair<Integer, List<Map<String, Object>>> pair = query.queryPrmUserInfosByJson(expression, start, 50);
                    List<Map<String, Object>> values = pair.getValue1();
                    for (Map<String, Object> value : values) {
                        Long prmId = Long.valueOf(value.get("id").toString());
                        Map<String, Object> m = new HashMap<>();
                        m.put(tagKey, -1);
                        writer.updateRecord(m, prmId, "system");
                    }
                    if (pair.getValue0() == 0 || values.size() < size) {
                        break;
                    } else {
                        start = start + size;
                    }
                }
                DataDefinition.updateTaskProgressStatus(DataDefinitionCacheManager.dataSource, tagKey, Constant.TAG_TASK_STATUS_SUCCESS);
            }catch(HappyQueryException e){
                LOG.error("tag task execute failed!expression:{}, tagKey:{}", expression, tagKey, e);
                DataDefinition.updateTaskProgressStatus(DataDefinitionCacheManager.dataSource, tagKey, Constant.TAG_TASK_STATUS_FAILED);
            }catch(Exception e){
                LOG.error("tag task execute failed!expression:{}, tagKey:{}, unexpected exception.", expression, tagKey, e);
                DataDefinition.updateTaskProgressStatus(DataDefinitionCacheManager.dataSource, tagKey, Constant.TAG_TASK_STATUS_FAILED);
            }
        }
    }
}
