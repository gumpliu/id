package com.yss.id.client.core.model.snowflake;

import com.yss.id.client.core.Constants;
import com.yss.id.client.core.model.BaseBuffer;
import com.yss.id.client.core.model.segment.Segment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Description: 雪花模式返回结构
 * @Author gumpLiu
 * @Date 2020-03-30
 * @Version V1.0
 **/
public class SnowflakeBuffer extends BaseBuffer<Snowflake> {

    public SnowflakeBuffer(){
        buffers =  new Snowflake[]{new Snowflake(this), null};
    }

    @Override
    public String nextId(Snowflake currentBuffer) {
        return currentBuffer.getQueue().remove();
    }

    @Override
    public boolean switchBufer(Snowflake currentBuffer) {
        return currentBuffer.getQueue().isEmpty();
    }

    @Override
    public boolean isloadNextBuffer(Snowflake currentBuffer, Snowflake nextBuffer) {

        //已经获取下一缓存信息
        if(nextBuffer != null && !nextBuffer.getQueue().isEmpty()){
            return false;
        }

        BigDecimal currentThreshold = BigDecimal.valueOf(currentBuffer.getStep())
                .subtract(BigDecimal.valueOf(currentBuffer.getQueue().size()))
                .divide(BigDecimal.valueOf(currentBuffer.getStep()))
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return currentThreshold.intValue() > Constants.ID_THRESHOLDVALUE;
    }

}
