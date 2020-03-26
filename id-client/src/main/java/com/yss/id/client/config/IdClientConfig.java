package com.yss.id.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: idClient相关配置
 * @Author gumpLiu
 * @Date 2020-03-26
 * @Version V1.0
 **/
@ConfigurationProperties(prefix = "id")
public class IdClientConfig {

    private IdServer server;

    public IdServer getServer() {
        return server;
    }

    public void setServer(IdServer server) {
        this.server = server;
    }

    public static class IdServer{

        /** id.server地址，多个以","分隔 **/
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
