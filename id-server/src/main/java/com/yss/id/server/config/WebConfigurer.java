package com.yss.id.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebConfigurer implements WebMvcConfigurer {

    @Autowired
    IdServerProperties idServerProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        IdServerInterceptor loggerInterceptor = new IdServerInterceptor(idServerProperties);

        registry.addInterceptor(loggerInterceptor).addPathPatterns("/**");
    }

}