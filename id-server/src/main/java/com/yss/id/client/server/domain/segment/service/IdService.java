package com.yss.id.client.server.domain.segment.service;

import com.yss.id.client.core.model.SegmentId;

/**
 * @Author: gumpLiu
 * @Date: 2020-03-24 10:21
 */
public interface IdService {

    /**
     * 根据bizTag获取最大maxId及step
     * @param bizTag
     * @return
     */
    SegmentId getSegment(String bizTag);

}
