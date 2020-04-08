package com.yss.id.client.core.model.snowflake;

import java.util.Queue;

/**
 * @Description: Snowflake 缓存类
 * @Author gumpLiu
 * @Date 2020-04-08
 * @Version V1.0
 **/
public class Snowflake {

    private Queue<String> queue;

    private int step;

    private SnowflakeBuffer snowflakeBuffer;

    public Snowflake(SnowflakeBuffer snowflakeBuffer){
        this.snowflakeBuffer = snowflakeBuffer;
    }

    public Queue<String> getQueue() {
        return queue;
    }

    public void setQueue(Queue<String> queue) {
        this.queue = queue;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public SnowflakeBuffer getSnowflakeBuffer() {
        return snowflakeBuffer;
    }

    public void setSnowflakeBuffer(SnowflakeBuffer snowflakeBuffer) {
        this.snowflakeBuffer = snowflakeBuffer;
    }
}
