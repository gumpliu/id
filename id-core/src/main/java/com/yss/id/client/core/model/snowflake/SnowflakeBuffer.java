package com.yss.id.client.core.model.snowflake;

import com.yss.id.client.core.model.BaseBuffer;
import com.yss.id.client.core.model.segment.Segment;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Description: 雪花模式返回结构
 * @Author gumpLiu
 * @Date 2020-03-30
 * @Version V1.0
 **/
public class SnowflakeBuffer extends BaseBuffer<Queue> {

    public SnowflakeBuffer(){
        buffers =  new ConcurrentLinkedQueue[]{new ConcurrentLinkedQueue(), null};
    }

    @Override
    public String nextId(Queue currentBuffer) {
        return null;
    }

    @Override
    public boolean switchBufer(Queue currentBuffer) {
        return false;
    }

    @Override
    public boolean isloadNextBuffer(Queue currentBuffer, Queue nextBuffer) {
        return false;
    }


}
