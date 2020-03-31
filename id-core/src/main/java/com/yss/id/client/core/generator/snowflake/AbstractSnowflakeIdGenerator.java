package com.yss.id.client.core.generator.snowflake;

import com.yss.id.client.core.Constants;
import com.yss.id.client.core.generator.AbstractIdGenerator;
import com.yss.id.client.core.model.BaseBuffer;
import com.yss.id.client.core.model.snowflake.SnowflakeBuffer;
import com.yss.id.client.core.service.IdService;

import java.math.BigInteger;
import java.util.Queue;

/**
 * @Description: 雪花模式实现，直接从id-server获取id
 * @Author gumpLiu
 * @Date 2020-03-26
 * @Version V1.0
 **/
public abstract class AbstractSnowflakeIdGenerator extends AbstractIdGenerator<Queue> implements IdSnowflakeGenerator{

    public AbstractSnowflakeIdGenerator(IdService idService) {
        super(idService);
    }

    public String nextId(){

        return nextId(Constants.SNOWFLAKE_KEY);

    }

    @Override
    protected String nextId(Queue currentBuffer) {
        return null;
    }

    @Override
    protected boolean switchBufer(Queue currentBuffer) {
        return false;
    }

    /**
     * 获取 nextId
     *
     * @return
     */
    public String formatNextId(){

        SnowflakeBuffer snowflakeBuffer = (SnowflakeBuffer) getBuffer(Constants.SNOWFLAKE_KEY);

        Queue queue = snowflakeBuffer.getCurrent();

        return "";
    }

    @Override
    protected boolean isloadNextBuffer(Queue currentBuffer, Queue nextBuffer) {
        return false;
    }

    @Override
    protected void romoteLoadNextBuffer(String bizTag, BaseBuffer baseBuffer) {

    }

}
