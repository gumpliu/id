package com.yss.id.client.core.generator.snowflake;

import com.yss.id.client.core.Constants;
import com.yss.id.client.core.generator.AbstractIdGenerator;
import com.yss.id.client.core.model.BaseBuffer;
import com.yss.id.client.core.model.SegmentId;
import com.yss.id.client.core.model.SnowflakeId;
import com.yss.id.client.core.model.segment.Segment;
import com.yss.id.client.core.model.segment.SegmentBuffer;
import com.yss.id.client.core.model.snowflake.Snowflake;
import com.yss.id.client.core.model.snowflake.SnowflakeBuffer;
import com.yss.id.client.core.service.IdService;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 雪花模式实现，直接从id-server获取id
 * @Author gumpLiu
 * @Date 2020-03-26
 * @Version V1.0
 **/
public class SnowflakeBufferIdGenerator extends AbstractIdGenerator<Snowflake>{

    public SnowflakeBufferIdGenerator(IdService idService) {
        super(idService);
    }

    public String nextId(){
        return nextId(Constants.SNOWFLAKE_KEY);
    }

    @Override
    public BaseBuffer createBaseBuffer(String bizTag) {
        //远程调用服务获取
        SnowflakeBuffer snowflakeBuffer = new SnowflakeBuffer();
        snowflakeBuffer.setKey(bizTag);

        Snowflake snowflake = getSnowflake(snowflakeBuffer);

        snowflakeBuffer.setStep(snowflake.getStep());

        snowflakeBuffer.getBuffers()[snowflakeBuffer.getCurrentPos()] = snowflake;

        return snowflakeBuffer;
    }

    @Override
    protected void romoteLoadNextBuffer(String bizTag) {
        //远程调用服务获取segment
        SnowflakeBuffer snowflakeBuffer = (SnowflakeBuffer) baseMap.get(bizTag);

        Snowflake snowflake = getSnowflake(snowflakeBuffer);

        snowflakeBuffer.getBuffers()[snowflakeBuffer.nextPos()] = snowflake;
    }

    /**
     * 获取segmentId
     * @param segmentBuffer
     * @return
     */
    private Snowflake getSnowflake(SnowflakeBuffer snowflakeBuffer){
        SnowflakeId snowflakeId = idService.getSnowflakeId();

        Snowflake snowflake = new Snowflake(snowflakeBuffer);
        snowflake.setStep(snowflakeId.getStep());
        snowflake.setQueue(new LinkedList<String>(snowflakeId.getIds()));

        return snowflake;
    }
}
