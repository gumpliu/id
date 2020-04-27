package com.yss.id.core.generator.snowflake.impl;

import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.exception.IdException;
import com.yss.id.core.generator.snowflake.SnowflakeBufferIdGenerator;
import com.yss.id.core.generator.snowflake.IdSnowflakeGenerator;
import com.yss.id.core.service.IdService;

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
    public String getSnowflakeNextId(IDFormatEnum format) {

        if(format == null){
            throw new IdException("getSnowflakeNextId format Can not be empty ！！");
        }

        return nextId(format);
    }
}
