package com.yss.id.client;

import com.yss.id.client.core.generator.IdGenerator;
import com.yss.id.client.core.factory.IdGeneratorFactory;

/**
 * @Description: id 获取类
 * @Author gumpLiu
 * @Date 2020-03-25
 * @Version V1.0
 **/
public class IdUtil {

    private static IdGenerator idGenerator = IdGeneratorFactory.getIdGenerator();

    /**
     * id为自增，不限制id长度
     *
     * @param bizTag 业务类型, 与t_alloc表中biz_tag对应
     * @return
     */
    public static String getSegmentNextId(String bizTag){
        return idGenerator.getSegmentNextId(bizTag);
    }

    /**
     * 返回id为固定长度，默认长度为6，如1 返回为000001
     *
     * @param bizTag 业务类型, 与t_alloc表中biz_tag对应
     * @return
     */
    public static String getSegmentFixedLengthNextId(String bizTag){
        return idGenerator.getSegmentFixedLengthNextId(bizTag);
    }

    /**
     * 返回id 添加前缀，id为固定长度，默认长度为6，如1 返回为000001
     * @param prefix 前缀
     * @param bizTag 业务类型, 与t_alloc表中biz_tag对应
     * @return
     */
    public static String getSegmentFixedLengthNextId(String prefix, String bizTag){
        return idGenerator.getSegmentFixedLengthNextId(prefix, bizTag);
    }

    /**
     * 返回id为固定长度，设置长度为{length}，如length=3 返回为001
     *
     * @param bizTag 业务类型, 与t_alloc表中biz_tag对应
     * @param length id长度
     * @return
     */
    public static String getSegmentFixedLengthNextId(String bizTag, int length){
        return idGenerator.getSegmentFixedLengthNextId(bizTag, length);
    }

    /**
     * 返回id为固定长度并添加前缀，
     * 设置长度为{length}，前缀为YSS 如length=3 返回为YSS001
     * @param prefix 前缀
     * @param bizTag 业务类型, 与t_alloc表中biz_tag对应
     * @param length id长度
     * @return
     */
    public static String getSegmentFixedLengthNextId(String prefix, String bizTag, int length){
        return idGenerator.getSegmentFixedLengthNextId(prefix, bizTag, length);
    }

    /**
     * 雪花模式返回值固定为20位
     *
     * @return
     */
    public static String getSnowflakeNextId(){
        return idGenerator.getSnowflakeNextId();
    }

    /**
     * 获取snowflake模式id,返回id值。可以设置时间返回格式，最低为秒。使用枚举限制。
     *
     * @param format
     * @return
     */
    public static String getSnowflakeNextId(Enum format){
        return idGenerator.getSnowflakeNextId(format);
    }
}
