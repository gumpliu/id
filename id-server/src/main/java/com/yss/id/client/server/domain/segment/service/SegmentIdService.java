package com.yss.id.client.server.domain.segment.service;

import com.yss.id.client.core.model.SegmentId;

/**
 * @Author: gumpLiu
 * @Date: 2020-03-24 10:21
 */
public interface SegmentIdService {

    /**
     * 根据bizTag获取最大maxId及step
     * @param bizTag
     * @return
     */
    SegmentId getSegment(String bizTag);

    /**
     * 获取segment，当前号段 maxId > 最大值时，初始化号段。
     * 例：maxLength = 4，最大值为9999。
     * 如当前maxId > 9999时，初始化号段并返回初始化后的maxId(即step值)
     *
     * @param bizTag
     * @param length
     * @return
     */
    SegmentId getSegment(String bizTag, int length);

    /**
     * 初始化号段值
     *
     * @param bizTag
     * @return
     */
    SegmentId initSegment(String bizTag);

}
