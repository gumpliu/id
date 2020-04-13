package com.yss.id.core.generator.snowflake;

import com.yss.id.core.constans.IDFormatEnum;

/**
 * @Author: gumpLiu
 * @Date: 2020-03-30 9:53
 */
public interface IdSnowflakeGenerator {

    /**
     * 获取snowflake模式id,返回id值，确定返回长度请查看{@link IDFormatEnum}
     *
     * @param format
     * @return
     */
    String getSnowflakeNextId(IDFormatEnum format);

}
