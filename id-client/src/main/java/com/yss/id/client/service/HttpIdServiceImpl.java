package com.yss.id.client.service;

import com.alibaba.fastjson.JSON;
import com.yss.id.client.config.IdClientConfig;
import com.yss.id.core.constans.Constants;
import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.exception.IdException;
import com.yss.id.core.model.SegmentId;
import com.yss.id.core.model.SnowflakeId;
import com.yss.id.core.service.IdService;
import com.yss.id.core.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

/**
 *
 * @Description: 通过http请求id-server 获取唯一id值
 * @Author gumpLiu
 * @Date 2020-03-25
 * @Version V1.0
 **/
public class HttpIdServiceImpl implements IdService {

    private static final Logger logger = LoggerFactory.getLogger(HttpIdServiceImpl.class);

    private static int readTimeout = 60000;

    private static int connectTimeout = 60000;

    @Autowired
    private IdClientConfig clientConfig;

    @Override
    public SegmentId getSegmentId(String bizTag) {
        String url = MessageFormat.format(Constants.SEGEMNT_URL, chooseService(), bizTag);

        return romoteLoadSegment(url);
    }

    @Override
    public SegmentId getSegmentId(String bizTag, int length) {
        String url = MessageFormat.format(Constants.SEGEMNT_FIEXD_URL, chooseService(), bizTag, length);

        return romoteLoadSegment(url);
    }

    @Override
    public SegmentId initSegmentId(String bizTag) {

        String url = MessageFormat.format(Constants.SEGEMNT_INIT_URL, chooseService(), bizTag);

        return romoteLoadSegment(url);
    }

    @Override
    public SnowflakeId getSnowflakeId(IDFormatEnum format) {

        String url = MessageFormat.format(Constants.SWONFLAKE_URL, chooseService(), format.name());


        return romoteLoadSnowflake(url);
    }

    /**
     * 远程通过http调用id-server服务
     *
     * @param url
     * @return
     */
    private SegmentId romoteLoadSegment(String url){

        String response = HttpUtils.post(url,readTimeout, connectTimeout);

        if (response == null || "".equals(response.trim())) {
            logger.error("romote load next segment error，id-server exception or segment enable is false");
            throw new IdException("romote load next segment error response is null! ");
        }
        SegmentId segmentId  = JSON.parseObject(response, SegmentId.class);

        return segmentId;
    }

    /**
     * 远程通过http调用id-server服务
     *
     * @param url
     * @return
     */
    private SnowflakeId romoteLoadSnowflake(String url){
        String response = HttpUtils.post(url, readTimeout, connectTimeout);
        if (response == null || "".equals(response.trim())) {
            logger.error("romote load next snowflake error，id-server exception or snowflake enable is false!");
            return null;
        }

        return JSON.parseObject(response, SnowflakeId.class);
    }

    /**
     * 获取配置url地址
     * @return
     */
    private String chooseService() {
        String urls = clientConfig.getServer().getUrl();

        List<String> serverList = CollectionUtils.arrayToList(urls.split(","));

        String url = "";
        if (serverList != null && serverList.size() == 1) {
            url = serverList.get(0);
        } else if (serverList != null && serverList.size() > 1) {
            Random r = new Random();
            url = serverList.get(r.nextInt(serverList.size()));
        }

        return url;
    }



}
