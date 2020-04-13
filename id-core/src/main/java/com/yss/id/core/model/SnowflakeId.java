package com.yss.id.core.model;

import java.util.List;

/**
 * @Description: Snowflake id
 * @Author gumpLiu
 * @Date 2020-04-08
 * @Version V1.0
 **/
public class SnowflakeId {

    private List<String> ids;

    private int step;

    public SnowflakeId(){}

    public SnowflakeId(List<String> ids, int step){
        this.ids = ids;
        this.step = step;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
