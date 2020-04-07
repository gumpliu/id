package com.yss.id.client.core.service;

import com.yss.id.client.core.model.SegmentId;

import java.util.List;

/**
 * @Description: 获取号段及雪花模式ids
 * @Author gumpLiu
 * @Date 2020-03-25
 * @Version V1.0
 **/
public interface IdService {

    /**
     * 获取SegmentId
     *
     * @param bizTag 业务类型
     * @return
     */
    SegmentId getSegmentId(String bizTag);

    /**
     * 获取SegmentId
     *
     * @param bizTag 业务类型
     * @param length maxId最大长度
     * @return
     */
    SegmentId getSegmentId(String bizTag, int length);

    /**
     * 初始segmentId
     *
     * @param bizTag 业务类型
     * @return
     */
    SegmentId initSegmentId(String bizTag);

    /**
     * 批量获取雪花模式id，返回个数在id-server端配置
     *
     * @return
     */
    List<String> getSnowflakeId();
}
