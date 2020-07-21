package com.xxl.job.executor.service.jobhandler;

import java.util.Arrays;
import java.util.Optional;

public enum ShardingXxlJobParamEnum {
    DEMO_PARAM("demo","测试demo")
    ;

    private String param;

    private String desc;

    ShardingXxlJobParamEnum(String param, String desc) {
        this.param = param;
        this.desc = desc;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public static  ShardingXxlJobParamEnum getEnumByParam(String param){
        return  Arrays.stream(values()).filter(p -> p.getParam().equals(param)).findFirst().orElse(null);
    }
}
