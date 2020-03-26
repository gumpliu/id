package com.yss.id.client.core.generator;

/**
 * id 生成器
 *
 * @Author: gumpLiu
 * @Date: 2020-03-25 14:55
 */
public interface IdGenerator {

    /**
     * id为自增，不限制id长度
     *
     * @param bizTag 业务类型, 与t_alloc表中biz_tag对应
     * @return
     */
    String getSegmentNextId(String bizTag);

    /**
     * 返回id为固定长度，默认长度为6，如1 返回为000001
     *
     * @param bizTag 业务类型, 与t_alloc表中biz_tag对应
     * @return
     */
    String getSegmentFixedLengthNextId(String bizTag);

    /**
     * 返回id 添加前缀，id为固定长度，默认长度为6，如1 返回为000001
     * @param prefix 前缀
     * @param bizTag 业务类型, 与t_alloc表中biz_tag对应
     * @return
     */
    String getSegmentFixedLengthNextId(String prefix, String bizTag);

    /**
     * 返回id为固定长度，设置长度为{length}，如length=3 返回为001
     *
     * @param bizTag 业务类型, 与t_alloc表中biz_tag对应
     * @param length id长度
     * @return
     */
    String getSegmentFixedLengthNextId(String bizTag, int length);

    /**
     * 返回id为固定长度并添加前缀，
     * 设置长度为{length}，前缀为YSS 如length=3 返回为YSS001
     * @param prefix 前缀
     * @param bizTag 业务类型, 与t_alloc表中biz_tag对应
     * @param length id长度
     * @return
     */
    String getSegmentFixedLengthNextId(String prefix, String bizTag, int length);

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
