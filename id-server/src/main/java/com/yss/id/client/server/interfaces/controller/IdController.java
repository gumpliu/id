package com.yss.id.client.server.interfaces.controller;

import com.yss.id.client.server.domain.segment.model.SegmentId;
import com.yss.id.client.server.domain.segment.service.IdService;
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
    IdService segmentService;

    /**
     * 获取segment
     * @param bizTag
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/segment/{bizTag}", method = RequestMethod.GET, produces="application/json")
    public SegmentId getSegment(@PathVariable String bizTag) {

        return segmentService.getSegment(bizTag);
    }

}
