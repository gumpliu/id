package com.yss.id.client.service;

import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.model.SegmentId;
import com.yss.id.core.model.SnowflakeId;
import com.yss.id.core.service.IdService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface EurekaFeignRemote extends IdService {

    /**
     * 获取SegmentId
     *
     * @param bizTag 业务类型
     * @return
     */
    @Override
    @RequestMapping(value="/id/segment/{bizTag}", method = RequestMethod.POST, produces="application/json")
    SegmentId getSegmentId(@PathVariable(value = "bizTag") String bizTag);

    /**
     * 获取SegmentId
     *
     * @param bizTag 业务类型
     * @param length maxId最大长度
     * @return
     */
    @Override
    @RequestMapping(value="/id/segment/{bizTag}/{length}", method = RequestMethod.POST, produces="application/json")
    SegmentId getSegmentId(@PathVariable(value = "bizTag") String bizTag, @PathVariable(value = "length") int length);

    /**
     * 初始segmentId
     *
     * @param bizTag 业务类型
     * @return
     */
    @Override
    @RequestMapping(value="/id/initSegment/{bizTag}", method = RequestMethod.POST, produces="application/json")
    SegmentId initSegmentId(@PathVariable(value = "bizTag") String bizTag);

    /**
     * 批量获取雪花模式id，返回个数在id-server端配置
     *
     * @return
     */
    @Override
    @RequestMapping(value="/id/snowflake/{format}", method = RequestMethod.POST, produces="application/json")
    SnowflakeId getSnowflakeId(@PathVariable(value = "format") IDFormatEnum format);


}
