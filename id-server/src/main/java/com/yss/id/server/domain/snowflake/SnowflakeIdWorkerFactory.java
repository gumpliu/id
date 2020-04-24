package com.yss.id.server.domain.snowflake;

import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.util.SnowflakeIdWorker;
import com.yss.id.server.config.IdServerProperties;
import com.yss.id.server.util.BeanUtil;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

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
                    IdServerProperties idServerProperties = BeanUtil.getBean(IdServerProperties.class);

                    SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(format, idServerProperties.getSnowflake().getStep());
                    int workerId = idServerProperties.getSnowflake().getWorkerId();
                    snowflakeIdWorker.init(workerId,workerId);

                    snowflakeIdWorkers.put(format, snowflakeIdWorker);
                }
            }
        }

        return snowflakeIdWorkers.get(format);
    }

}
