package com.yss.id.client.service;

import com.alibaba.fastjson.JSON;
import com.yss.id.client.config.IdClientConfig;
import com.yss.id.client.core.model.SegmentId;
import com.yss.id.client.core.service.IdService;
import com.yss.id.client.core.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

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
        String url = chooseService(bizTag);
        String response = HttpUtils.post(url,10000, 1000);
        logger.info("tinyId client getNextSegmentId end, response:" + response);
        if (response == null || "".equals(response.trim())) {
            return null;
        }
        SegmentId segmentId  = JSON.parseObject(response, SegmentId.class);

        return segmentId;
    }

    @Override
    public SegmentId initSegmentId(String bizTag) {
        return null;
    }

    @Override
    public List<String> getSnowflakeId() {
        return null;
    }


    private String chooseService(String bizType) {
        String urls = clientConfig.getServer().getUrl();

        List<String> serverList = CollectionUtils.arrayToList(urls.split(","));

        String url = "";
        if (serverList != null && serverList.size() == 1) {
            url = serverList.get(0);
        } else if (serverList != null && serverList.size() > 1) {
            Random r = new Random();
            url = serverList.get(r.nextInt(serverList.size()));
        }
        url += "/" + bizType;
        return url;
    }



}
