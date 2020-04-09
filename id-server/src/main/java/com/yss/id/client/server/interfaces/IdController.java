package com.yss.id.client.server.interfaces;

import com.yss.id.client.core.constans.IDFormatEnum;
import com.yss.id.client.core.exception.IdException;
import com.yss.id.client.core.model.SegmentId;
import com.yss.id.client.core.model.SnowflakeId;
import com.yss.id.client.server.domain.segment.service.SegmentIdService;
import com.yss.id.client.server.domain.snowflake.SnowflakeIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *  todo 压力测试，因为使用了数据库锁，2、验证数据锁是否生效 3、锁优化
 *
 * @Description: id 获取
 * @Author gumpLiu
 * @Date 2020-03-24
 * @Version V1.0
 **/
@Controller
@RequestMapping("/id")
public class IdController {

    @Autowired
    SegmentIdService segmentService;

    @Autowired
    SnowflakeIdService snowflakeIdService;

    /**
     * 获取segment
     * @param bizTag
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/segment/{bizTag}", method = RequestMethod.POST, produces="application/json")
    public SegmentId getSegment(@PathVariable String bizTag) {

        return segmentService.getSegment(bizTag);
    }

    /**
     * 获取segment，当前号段 maxId > 最大值时，初始化号段。
     * 例：maxLength = 4，最大值为9999。
     * 如当前maxId > 9999时，初始化号段并返回初始化后的maxId(即step值)
     *
     * @param bizTag
     * @param length 最大长度
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/segment/{bizTag}/{length}", method = RequestMethod.POST, produces="application/json")
    public SegmentId getSegment(@PathVariable String bizTag,
                                @PathVariable int length) {

        return segmentService.getSegment(bizTag, length);
    }

    /**
     * 初始化号段并返回初始号段值
     * @param bizTag
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/initSegment/{bizTag}", method = RequestMethod.POST, produces="application/json")
    public SegmentId initSegment(@PathVariable String bizTag) {

        return segmentService.initSegment(bizTag);
    }

    /**
     * 雪花模式 ids
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/snowflake/{format}", method = RequestMethod.POST, produces="application/json")
    public SnowflakeId snowflake(@PathVariable  String format) {

        IDFormatEnum idFormat = IDFormatEnum.valueOf(format);

        if(idFormat == null){
            throw new IdException("id format is error! ");
        }

        return snowflakeIdService.getIds(idFormat);
    }

}
