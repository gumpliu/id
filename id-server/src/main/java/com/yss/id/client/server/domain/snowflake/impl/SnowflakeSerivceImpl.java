package com.yss.id.client.server.domain.snowflake.impl;

import com.yss.id.client.core.exception.IdException;
import com.yss.id.client.core.model.SnowflakeId;
import com.yss.id.client.core.util.SnowflakeIdWorker;
import com.yss.id.client.server.config.IdServerProperties;
import com.yss.id.client.server.domain.snowflake.SnowflakeIdService;
import com.yss.id.client.server.domain.snowflake.SnowflakeIdWorkerFactory;
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

    private SnowflakeIdWorker worker = SnowflakeIdWorkerFactory.getInstance();


    @Override
    public SnowflakeId getIds() {
        //todo 统一处理
        enable();

        int step = idServerProperties.getSnowflake().getStep();

        List<String> ids = new ArrayList<String>();

        for(int i = 0; i < step; i++){
            ids.add(String.valueOf(worker.genNextId()));
        }

        return new SnowflakeId(ids, step);
    }

    private void enable(){
        if(!idServerProperties.getSnowflake().isEnable()){
            //todo 指定错误码
            throw new IdException("snowflake 模式不可用");
        }
    }
}
