package com.yss.id.client.server.domain.segment.service.impl;

import com.yss.id.client.server.baseplatform.config.IdServerProperties;
import com.yss.id.client.server.domain.segment.entity.AllocEntity;
import com.yss.id.client.server.domain.segment.model.SegmentId;
import com.yss.id.client.server.domain.segment.repository.AllocRepository;
import com.yss.id.client.server.domain.segment.service.IdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;

/**
 * @Description: 号段模式获取segment
 * @Author gumpLiu
 * @Date 2020-03-24
 * @Version V1.0
 **/
@Service
public class IdServiceImpl implements IdService {

    @Autowired
    IdServerProperties idServerProperties;

    @Autowired
    AllocRepository allocRepository;

    /**
     * 号段Segment 获取
     * @param bizTag
     * @return
     */
    @Override
    @Transactional
    public SegmentId getSegment(String bizTag) {

        //todo 是否在server端做重试机制

        AllocEntity allocEntity = allocRepository.findByBizTag(bizTag);
        int step = idServerProperties.getSegement().getStep();
        Date now = new Date();
        if(allocEntity == null){
            allocEntity = new AllocEntity();
            allocEntity.setBizTag(bizTag);
            allocEntity.setMaxId(BigInteger.ZERO);
            allocEntity.setStep(idServerProperties.getSegement().getStep());
            allocEntity.setCreateTime(now);
            allocEntity.setUpdateTime(now);
            allocRepository.save(allocEntity);
        }

        SegmentId segment = new SegmentId();
        segment.setMaxId(allocEntity.getMaxId().add(BigInteger.valueOf(step)).longValue());
        segment.setStep(step);
        allocRepository.updateByBizTag(bizTag);

        return segment;
    }
}
