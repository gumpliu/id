package com.yss.id.client.core.generator.snowflake.impl;

import com.yss.id.client.core.generator.snowflake.AbstractSnowflakeIdGenerator;
import com.yss.id.client.core.service.IdService;

import java.util.Queue;

/**
 * @Description: 雪花模式 实现
 * @Author gumpLiu
 * @Date 2020-03-30
 * @Version V1.0
 **/
public class IdSnowflakeGeneratorImpl extends AbstractSnowflakeIdGenerator {

    public IdSnowflakeGeneratorImpl(IdService idService) {
        super(idService);
    }

    @Override
    public String getSnowflakeNextId() {
        return nextId();
    }

    @Override
    public String getSnowflakeNextId(Enum format) {
        return formatNextId();
    }
}
