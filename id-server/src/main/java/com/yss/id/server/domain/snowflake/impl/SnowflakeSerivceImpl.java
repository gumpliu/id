package com.yss.id.server.domain.snowflake.impl;

import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.model.SnowflakeId;
import com.yss.id.server.config.IdServerProperties;
import com.yss.id.server.domain.snowflake.SnowflakeIdService;
import com.yss.id.server.domain.snowflake.SnowflakeIdWorkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: snowflake 模式
 * @Author gumpLiu
 * @Date 2020-04-07
 * @Version V1.0
 **/
@Service
public class SnowflakeSerivceImpl implements SnowflakeIdService {

    @Autowired
    private IdServerProperties idServerProperties;

    @Override
    public SnowflakeId getIds(IDFormatEnum format) {

        int step = idServerProperties.getSnowflake().getStep();

        List<String> ids = new ArrayList<String>();

        for(int i = 0; i < step; i++){
            ids.add(String.valueOf(SnowflakeIdWorkerFactory.getInstance(format).genNextId(format)));
        }

        return new SnowflakeId(ids, step);
    }
}
