package com.yss.id.client.core.generator.snowflake;

/**
 * @Author: gumpLiu
 * @Date: 2020-03-30 9:53
 */
public interface IdSnowflakeGenerator {

    /**
     * 雪花模式返回值固定为20位
     *
     * @return
     */
    String getSnowflakeNextId();

    /**
     * 获取snowflake模式id,返回id值。可以设置时间返回格式，最低为秒。使用枚举限制。
     *
     * @param format
     * @return
     */
    String getSnowflakeNextId(Enum format);

}
