package com.yss.id.client.server.baseplatform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: id-server相关配置
 * @Author gumpLiu
 * @Date 2020-01-09
 * @Version V1.0
 **/
@ConfigurationProperties(prefix = "id")
public class IdServerProperties {

    private SegmentProperties segement;

    private SnowflakeProperties snowflake;

    public SegmentProperties getSegement() {
        return segement;
    }

    public void setSegement(SegmentProperties segement) {
        this.segement = segement;
    }

    public SnowflakeProperties getSnowflake() {
        return snowflake;
    }

    public void setSnowflake(SnowflakeProperties snowflake) {
        this.snowflake = snowflake;
    }

    public static class SegmentProperties{

        /** segment 步长 **/
        private int step = 1000;

        /** 号段模式是否可用 false-不可用，true-可用 **/
        private boolean enable = true;

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }

    public static class SnowflakeProperties{

        /** 雪花模式 步长 **/
        private int step = 1000;

        /** 雪花模式是否可用 false-不可用，true-可用 **/
        private boolean enable = true;

        /** 雪花模式 workerId 全局唯一 **/
        private int workerId;

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public int getWorkerId() {
            return workerId;
        }

        public void setWorkerId(int workerId) {
            this.workerId = workerId;
        }
    }
}






