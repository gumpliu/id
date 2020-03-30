package com.yss.id.client.server;

import com.yss.id.client.server.config.IdServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * todo id-server 启动校验
 * todo biz_tag 管理，1-直接创建 2、上线时提供DB脚本。暂时为没有直接创建！
 *
 */
@SpringBootApplication
@EnableConfigurationProperties(IdServerProperties.class)
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
//	
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        // 注意这里要指向原先用main方法执行的Application启动类
//        return builder.sources(Application.class);
//    }
}