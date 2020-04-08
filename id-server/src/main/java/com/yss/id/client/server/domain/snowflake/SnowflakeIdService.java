package com.yss.id.client.server.domain.snowflake;

import com.yss.id.client.core.model.SnowflakeId;
import com.yss.id.client.server.config.IdServerProperties;

/**
 * @Author: gumpLiu
 * @Date: 2020-04-07 16:22
 */
public interface SnowflakeIdService {

    /**
     * 批量获取id，根据雪花算法生成
     *           返回个数默认为1000，可用看{@link IdServerProperties} 项目配置
     *
     * @return SnowflakeId
     */
    SnowflakeId getIds();

}
