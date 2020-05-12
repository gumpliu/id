package com.yss.id.client;

import com.yss.id.client.config.IdClientConfig;
import com.yss.id.client.service.HttpIdServiceImpl;
import com.yss.id.client.util.BeanUtil;
import com.yss.id.core.generator.segment.IdSegmentGenerator;
import com.yss.id.core.generator.segment.impl.IdSegmentGeneratorImpl;
import com.yss.id.core.generator.snowflake.IdSnowflakeGenerator;
import com.yss.id.core.generator.snowflake.impl.IdSnowflakeGeneratorImpl;
import com.yss.id.core.service.IdService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * @Description: id-client 启动类
 * @Author gumpLiu
 * @Date 2020-03-30
 * @Version V1.0
 **/
@Configuration
@EnableConfigurationProperties({IdClientConfig.class})
public class IdClientAutoConfiguration {

    @Bean
    public BeanUtil idClientBeanUtil(){
        return new BeanUtil();
    }

    @Bean
    @ConditionalOnMissingBean(IdService.class)
    public IdService httpService(){
        return new HttpIdServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(IdSnowflakeGenerator.class)
    public IdSnowflakeGenerator snowflakeGenerator(IdService idService){
        return new IdSnowflakeGeneratorImpl(idService);
    }

    @Bean
    @ConditionalOnMissingBean(IdSegmentGenerator.class)
    public IdSegmentGenerator segmentGenerator(IdService idService){
        return new IdSegmentGeneratorImpl(idService);
    }
}
