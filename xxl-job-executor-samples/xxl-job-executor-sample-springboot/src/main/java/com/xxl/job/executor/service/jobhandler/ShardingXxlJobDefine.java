package com.xxl.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSON;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.util.ShardingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述: <br>
 * 〈分片广播父类定义, 多个实例同时执行任务〉
 * @Author: linyuan1
 * @Date: 2020/7/20 16:33
 */
public abstract class ShardingXxlJobDefine {

    @Autowired
    private RedisTemplate redisTemplate;

    static final String JOBNAME="common-sharding-job";

    //分片等待数据超时时间 30s
    static final Long waitTime=1000L*30;

    //数据准备状态 , 0 无数据, 1 数据准备完成
    static  String READY_KEY=JOBNAME+":%s"+":ready";

    static  String DATA_KEY=JOBNAME+":%s"+":ids";

    //防止实例挂掉,key未删除
    static long  REDIS_KEY_TIME_OUT=1000L*10*100;

    /**
     * 功能描述: <br>
     * 〈准备数据:〉
     *  0号实例作为leader, 查出数据项的id , 并存入缓存,为其他节点分配任务
     * @Param: []
     * @Return: void
     * @Author: linyuan1
     * @Date: 2020/7/20 11:04
     */
    public  void prepareData(List<Long> allTaskIds, String param){
        try {
            String readyKey = String.format(READY_KEY, param);
            if (CollectionUtils.isEmpty(allTaskIds)){ //没有任务可执行
                redisTemplate.opsForValue().set(readyKey,"0",REDIS_KEY_TIME_OUT, TimeUnit.MILLISECONDS);
                return;
            }
            ShardingUtil.ShardingVO shardingVo = ShardingUtil.getShardingVo();
            int total = shardingVo.getTotal();
            int taskCount = allTaskIds.size();
            for (int i=0;i<total; i++){
                int partion = taskCount / total;
                List<Long> shardingIds;
                if (i<total-1){
                    shardingIds = allTaskIds.subList(partion * i, partion * (i + 1));
                }else {
                    shardingIds=allTaskIds.subList(partion*i,taskCount);
                }
                String redisDataKey = String.format(DATA_KEY, param);
                redisTemplate.opsForHash().put(redisDataKey,String.valueOf(i), JSON.toJSONString(shardingIds));

            }
            redisTemplate.opsForValue().set(readyKey,"1",REDIS_KEY_TIME_OUT, TimeUnit.MILLISECONDS);
        }catch (Exception ex){
                ex.printStackTrace();
        }

    }



    @XxlJob(JOBNAME)
    public ReturnT<String> runTask(String param){
        System.out.println("common-sharding-任务开始执行");
        ShardingUtil.ShardingVO shardingVo = ShardingUtil.getShardingVo();
        List<Long> allTaskIds=Collections.emptyList();
        if (shardingVo.getIndex()==0){
            allTaskIds = getAllTaskIds(param);
            prepareData(allTaskIds,param);
        }else{
            waitForData(param);
        }
        List<Long> taskDataIds = getShardingData(param);
        if (CollectionUtils.isEmpty(taskDataIds)){
            return new ReturnT<>(200,"没有需要执行的任务");
        }
        return excuteTask(taskDataIds);
    }

    /**
     * 功能描述: <br>
     * 〈0号节点之外的其他节点等待数据准备完成〉
     * @Param: []
     * @Return: boolean
     * @Author: linyuan1
     * @Date: 2020/7/20 16:56
     * @param param
     */
    protected boolean waitForData(String param){
        long time = System.currentTimeMillis();
        long endTime=time+waitTime;
        boolean getData=false;
        String readyKey = String.format(READY_KEY, param);
        while (System.currentTimeMillis()<endTime){
            String readyFlag= (String)redisTemplate.opsForValue().get(readyKey);
            if (StringUtils.isEmpty(readyFlag)){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else {
                getData=true;
                break;
            }
        }
        return getData;
    }

    public List<Long> getShardingData(String param){
        String readyKey = String.format(READY_KEY, param);
        String ready_flag = (String)redisTemplate.opsForValue().get(readyKey);
        if ("0".equals(ready_flag)||ready_flag==null){
            return null;
        }
        String redisDataKey = String.format(DATA_KEY, param);
        ShardingUtil.ShardingVO shardingVo = ShardingUtil.getShardingVo();
        String dataListStr = (String)redisTemplate.opsForHash().get(redisDataKey, String.valueOf(shardingVo.getIndex()));
        if (StringUtils.isEmpty(dataListStr)){
            return null;
        }
        List<Long> dataList = JSON.parseArray(dataListStr, Long.class);
        return  dataList;
    }




    /**
     * 功能描述: <br>
     * 〈具体的业务处理〉
     * @Param: [taskIds] 要执行的任务id
     * @Return: com.xxl.job.core.biz.model.ReturnT<java.lang.String>
     * @Author: linyuan1
     * @Date: 2020/7/20 16:47
     */
    public abstract ReturnT<String> excuteTask(List<Long> taskIds);


    /**
     * 功能描述: <br>
     * 〈查出所有的任务id列表〉
     * @Param: [param] 控制台参数
     * @Return: java.util.List<java.lang.Long>
     * @Author: linyuan1
     * @Date: 2020/7/20 16:46
     */
    public abstract List<Long> getAllTaskIds(String param);




}
