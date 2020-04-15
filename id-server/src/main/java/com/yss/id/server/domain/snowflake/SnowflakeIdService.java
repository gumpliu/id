package com.yss.id.server.domain.snowflake;

import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.model.SnowflakeId;
import com.yss.id.server.config.IdServerProperties;

import java.sql.Date;

/**
 * @Author: gumpLiu
 * @Date: 2020-04-07 16:22
 */
public interface SnowflakeIdService {

    /**
     * 批量获取id，根据雪花算法生成
     *           返回个数默认为1000，可用看{@link IdServerProperties} 项目配置
     *
     * @param format  IDFormatEnum 时间格式
     *
     * @return SnowflakeId
     */
    SnowflakeId getIds(IDFormatEnum format);

}
