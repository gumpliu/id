package com.yss.id.server.config;

import com.yss.id.server.util.SnowflakeCheckTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SnowflakeCheckTimeTask {

    private Logger logger = LoggerFactory.getLogger(SnowflakeCheckTimeTask.class);


    @Autowired
    private IdServerProperties idServerProperties;

    @Autowired
    private SnowflakeCheckTime snowflakeCheckTime;

    @Scheduled(cron = "0 */10 * * * ?")
    private void configureTasks() {

        try{
            snowflakeCheckTime.chekoutTime();
        }catch (Exception e){
            logger.error(e.getMessage());
            //关闭snowflake模式
            idServerProperties.getSnowflake().setEnable(false);
        }

    }
}