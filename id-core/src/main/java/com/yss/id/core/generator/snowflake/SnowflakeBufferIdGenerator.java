package com.yss.id.core.generator.snowflake;

import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.generator.AbstractIdGenerator;
import com.yss.id.core.model.BaseBuffer;
import com.yss.id.core.model.SnowflakeId;
import com.yss.id.core.model.snowflake.Snowflake;
import com.yss.id.core.model.snowflake.SnowflakeBuffer;
import com.yss.id.core.service.IdService;

import java.util.LinkedList;

/**
 * @Description: 雪花模式实现，直接从id-server获取id
 * @Author gumpLiu
 * @Date 2020-03-26
 * @Version V1.0
 **/
public class SnowflakeBufferIdGenerator extends AbstractIdGenerator<Snowflake> {

    public SnowflakeBufferIdGenerator(IdService idService) {
        super(idService);
    }

    public String nextId(IDFormatEnum format){
        return nextId(format.name());
    }

    @Override
    public BaseBuffer createBaseBuffer(String bizTag) {
        //远程调用服务获取
        SnowflakeBuffer snowflakeBuffer = new SnowflakeBuffer();
        snowflakeBuffer.setKey(bizTag);
        snowflakeBuffer.setFormat(IDFormatEnum.valueOf(bizTag));

        Snowflake snowflake = getSnowflake(snowflakeBuffer);

        snowflakeBuffer.setStep(snowflake.getStep());

        snowflakeBuffer.getBuffers()[snowflakeBuffer.getCurrentPos()] = snowflake;

        return snowflakeBuffer;
    }

    @Override
    protected Snowflake romoteLoadNextBuffer(String bizTag) {
        //远程调用服务获取segment
        SnowflakeBuffer snowflakeBuffer = (SnowflakeBuffer) baseMap.get(bizTag);

        return getSnowflake(snowflakeBuffer);
    }

    /**
     * 获取Snowflake
     * @param snowflakeBuffer
     * @return
     */
    private Snowflake getSnowflake(SnowflakeBuffer snowflakeBuffer){
        SnowflakeId snowflakeId = idService.getSnowflakeId(snowflakeBuffer.getFormat());

        Snowflake snowflake = new Snowflake(snowflakeBuffer);
        snowflake.setStep(snowflakeId.getStep());
        snowflake.setQueue(new LinkedList<String>(snowflakeId.getIds()));

        return snowflake;
    }
}
