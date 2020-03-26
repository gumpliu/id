package com.yss.id.client.core.service.impl;

import com.yss.id.client.core.model.SegmentId;
import com.yss.id.client.core.service.IdService;

import java.util.List;

/**
 * @Description: 通过http请求id-server 获取唯一id值
 * @Author gumpLiu
 * @Date 2020-03-25
 * @Version V1.0
 **/
public class HttpIdServiceImpl implements IdService {

    @Override
    public SegmentId getSegmentId(String bizTag) {
        return null;
    }

    @Override
    public SegmentId initSegmentId(String bizTag) {
        return null;
    }

    @Override
    public List<String> getSnowflakeId() {
        return null;
    }
}
