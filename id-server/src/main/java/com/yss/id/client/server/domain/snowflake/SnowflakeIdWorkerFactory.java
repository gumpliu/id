package com.yss.id.client.server.domain.snowflake;

import com.yss.id.client.core.constans.IDFormatEnum;
import com.yss.id.client.core.util.SnowflakeIdWorker;
import com.yss.id.client.server.config.BeanUtil;
import com.yss.id.client.server.config.IdServerProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: SnowflakeIdWorker 工厂类
 * @Author gumpLiu
 * @Date 2020-04-08
 * @Version V1.0
 **/
public class SnowflakeIdWorkerFactory {

    protected static Map<IDFormatEnum, SnowflakeIdWorker> snowflakeIdWorkers = new ConcurrentHashMap<>();

    public static SnowflakeIdWorker getInstance(IDFormatEnum format){
        if(!snowflakeIdWorkers.containsKey(format)){
            synchronized (SnowflakeIdWorkerFactory.class){
                if(!snowflakeIdWorkers.containsKey(format)){
                    SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();
                    IdServerProperties idServerProperties = BeanUtil.getBean(IdServerProperties.class);
                    int workerId = idServerProperties.getSnowflake().getWorkerId();
                    snowflakeIdWorker.init(workerId,workerId);

                    snowflakeIdWorkers.put(format, snowflakeIdWorker);
                }
            }
        }

        return snowflakeIdWorkers.get(format);
    }

}
