package com.yss.id.client.core.generator.snowflake.impl;

import com.yss.id.client.core.generator.snowflake.SnowflakeBufferIdGenerator;
import com.yss.id.client.core.generator.snowflake.IdSnowflakeGenerator;
import com.yss.id.client.core.service.IdService;

/**
 * @Description: 雪花模式 实现
 * @Author gumpLiu
 * @Date 2020-03-30
 * @Version V1.0
 **/
public class IdSnowflakeGeneratorImpl extends SnowflakeBufferIdGenerator implements IdSnowflakeGenerator {

    public IdSnowflakeGeneratorImpl(IdService idService) {
        super(idService);
    }

    @Override
    public String getSnowflakeNextId() {
        return nextId();
    }

    @Override
    public String getSnowflakeNextId(Enum format) {
        return "";
    }
}
