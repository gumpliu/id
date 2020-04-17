package com.yss.id.server.util;

import com.yss.id.core.exception.IdException;
import com.yss.id.server.config.IdServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @Description: snowflake模式 时间检验
 * @Author gumpLiu
 * @Date 2020-04-15
 * @Version V1.0
 **/
@Component
public class SnowflakeCheckTime {

    private static final Logger logger = LoggerFactory.getLogger(SnowflakeCheckTime.class);

    @Autowired
    private IdServerProperties idServerProperties;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

    private final static BigDecimal MAX_DIFFERENCE_VALUE = BigDecimal.valueOf(30000);


    @PostConstruct
    public void chekoutTime(){

        if(idServerProperties.getSnowflake().isEnable()){

            Timestamp timestamp = (Timestamp) entityManager.createNativeQuery(getDBTimeSql()).getSingleResult();

            long currentTime = System.currentTimeMillis();

            logger.info("checkout time, currentTime={},dbTime={}", currentTime, timestamp.getTime());

            BigDecimal differenceValue = BigDecimal.valueOf(timestamp.getTime()).subtract(BigDecimal.valueOf(currentTime));

            if(differenceValue.abs().compareTo(MAX_DIFFERENCE_VALUE) == 1){
                throw new IdException("Snowflake time is error!");
            }
        }
    }

    /**
     * 获取不同数据库系统时间sql
     * @return
     */
    private String getDBTimeSql(){

        String driverClassName =  configurableEnvironment.getProperty("spring.datasource.driver-class-name").toLowerCase();


        if(driverClassName.contains("oracle")){
           return  "select sysdate  from dual";
        }else if(driverClassName.contains("mysql")){
            return  "select now() ";
        }else if(driverClassName.contains("sqlserver")){
            return "select GETDATE() ";
        }else if(driverClassName.contains("db2")){
            return "select sysdate from dual";
        }else{
            throw new IdException("DB ONLY USE MYSQL OR ORACLE !");
        }
    }
}
