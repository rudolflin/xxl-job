package com.xxl.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.util.ShardingUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ShardingJobDemo extends ShardingXxlJobDefine {


    @Override
    public ReturnT<String> excuteTask(List<Long> taskIds) {

        return new ReturnT<>(200, JSON.toJSONString(taskIds));
    }

    @Override
    public List<Long> getAllTaskIds(String param) {
        if (ShardingXxlJobParamEnum.getEnumByParam(param)==ShardingXxlJobParamEnum.DEMO_PARAM){
            List<Long> allIds=new ArrayList<>();
            for (int i=0;i<107;i++){
                allIds.add((long) i);
            }
            return  allIds;
        }
        return null;
    }

}
