package com.yss.id.client.service;

import com.alibaba.fastjson.JSON;
import com.yss.id.client.config.IdClientConfig;
import com.yss.id.core.constans.Constants;
import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.model.SegmentId;
import com.yss.id.core.model.SnowflakeId;
import com.yss.id.core.service.IdService;
import com.yss.id.core.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 *
 * @Description: 通过http请求id-server 获取唯一id值
 * @Author gumpLiu
 * @Date 2020-03-25
 * @Version V1.0
 **/
public class HttpIdServiceImpl implements IdService {

    private static final Logger logger = Logger.getLogger(HttpIdServiceImpl.class.getName());

    @Autowired
    private IdClientConfig clientConfig;

    @Override
    public SegmentId getSegmentId(String bizTag) {
        String url = MessageFormat.format(Constants.SEGEMNT_URL, chooseService(), bizTag);

        return remoteLoadSegment(url);
    }

    @Override
    public SegmentId getSegmentId(String bizTag, int length) {
        String url = MessageFormat.format(Constants.SEGEMNT_FIEXD_URL, chooseService(), bizTag, length);

        return remoteLoadSegment(url);
    }

    @Override
    public SegmentId initSegmentId(String bizTag) {

        String url = MessageFormat.format(Constants.SEGEMNT_INIT_URL, chooseService(), bizTag);

        return remoteLoadSegment(url);
    }

    @Override
    public SnowflakeId getSnowflakeId(IDFormatEnum format) {

        String url = MessageFormat.format(Constants.SWONFLAKE_URL, chooseService(), format.name());


        return remoteLoadSnowflake(url);
    }

    /**
     * 远程通过http调用id-server服务
     *
     * @param url
     * @return
     */
    private SegmentId remoteLoadSegment(String url){
        //todo 添加重试机制
        String response = HttpUtils.post(url,5000, 5000);
        logger.info("id client remoteLoadSegment end, response:" + response);
        if (response == null || "".equals(response.trim())) {
            return null;
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
    private SnowflakeId remoteLoadSnowflake(String url){
        String response = HttpUtils.post(url,5000, 5000);
        logger.info("id client remoteLoadSnowflake end, response:" + response);
        if (response == null || "".equals(response.trim())) {
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
