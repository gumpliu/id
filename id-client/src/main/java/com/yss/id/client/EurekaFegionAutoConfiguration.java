package com.yss.id.client;

import com.yss.id.client.service.EurekaFeignRemote;
import com.yss.id.core.service.IdService;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(value = "eureka.client.serviceUrl.defaultZone")
@AutoConfigureBefore(IdClientAutoConfiguration.class)
@AutoConfigureAfter(FeignAutoConfiguration.class)
@Import(FeignClientsConfiguration.class)
public class EurekaFegionAutoConfiguration {

    @Bean
    public IdService remoteService(Decoder decoder, Client client, Encoder encoder, Contract contract) {

        EurekaFeignRemote eurekaFeignRemote =  Feign.builder()
                .client(client)
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)
                .target(EurekaFeignRemote.class,
                        "http://id-server");

        return eurekaFeignRemote;
    }
}