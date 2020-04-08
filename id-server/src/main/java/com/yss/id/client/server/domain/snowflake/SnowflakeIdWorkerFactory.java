package com.yss.id.client.server.domain.snowflake;

import com.yss.id.client.core.util.SnowflakeIdWorker;
import com.yss.id.client.server.config.BeanUtil;
import com.yss.id.client.server.config.IdServerProperties;

/**
 * @Description: SnowflakeIdWorker 工厂类
 * @Author gumpLiu
 * @Date 2020-04-08
 * @Version V1.0
 **/
public class SnowflakeIdWorkerFactory {

    private static SnowflakeIdWorker snowflakeIdWorker;

    public static SnowflakeIdWorker getInstance(){
        if(snowflakeIdWorker == null){
            synchronized (SnowflakeIdWorkerFactory.class){
                if(snowflakeIdWorker == null){
                    snowflakeIdWorker = new SnowflakeIdWorker();
                    IdServerProperties idServerProperties = BeanUtil.getBean(IdServerProperties.class);
                    int workerId = idServerProperties.getSnowflake().getWorkerId();
                    snowflakeIdWorker.init(workerId,workerId);
                }
            }
        }

        return snowflakeIdWorker;
    }

}
