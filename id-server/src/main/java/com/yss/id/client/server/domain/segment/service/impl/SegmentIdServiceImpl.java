package com.yss.id.client.server.domain.segment.service.impl;

import com.yss.id.client.core.exception.IdException;
import com.yss.id.client.core.model.SegmentId;
import com.yss.id.client.core.util.MathUtil;
import com.yss.id.client.server.config.IdServerProperties;
import com.yss.id.client.server.domain.segment.entity.AllocEntity;
import com.yss.id.client.server.domain.segment.repository.AllocRepository;
import com.yss.id.client.server.domain.segment.service.SegmentIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
public class SegmentIdServiceImpl implements SegmentIdService {

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

        enable();

        //todo 是否在server端做重试机制
        for(int i = 0; i < 5; i++){
            AllocEntity allocEntity = getAllocEntity(bizTag);

            int updateNum = allocRepository.updateByBizTag(bizTag, allocEntity.getVersion());

            if(updateNum == 1){
                return createSegmentId(allocEntity.getMaxId());
            }
        }
        throw new IdException("号段获取失败！");
    }

    @Override
    @Transactional
    public SegmentId getSegment(String bizTag, int length) {

        enable();

        long maxValue  = MathUtil.maxValue(bizTag, length);

        for(int i = 0; i < 5; i++){
            AllocEntity allocEntity = getAllocEntity(bizTag);

            BigInteger maxId = allocEntity.getMaxId();

            int updateNum;
            if(MathUtil.greaterThanEqual(allocEntity.getMaxId().longValue(), maxValue)){
                //初始化segment
                updateNum = allocRepository.initSegmentByBizTag(bizTag, allocEntity.getVersion());

                maxId = BigInteger.ZERO;
            }else{
                updateNum = allocRepository.updateByBizTag(bizTag, allocEntity.getVersion());
            }

            if(updateNum == 1){
                return createSegmentId(maxId, length);
            }
        }
        throw new IdException("号段获取失败！");


    }

    @Override
    @Transactional
    public SegmentId initSegment(String bizTag) {

        enable();

        for(int i = 0; i < 5; i++){
            AllocEntity allocEntity = getAllocEntity(bizTag);

            int updateNum = allocRepository.initSegmentByBizTag(bizTag, allocEntity.getVersion());

            if(updateNum == 1){
                return createSegmentId(BigInteger.ZERO);
            }
        }
        throw new IdException("号段获取失败！");
    }


    /**
     * 获取 AllocEntity
     * @param bizTag
     * @return
     */
    private AllocEntity getAllocEntity(String bizTag){

        AllocEntity allocEntity = allocRepository.findByBizTag(bizTag);

        if(allocEntity == null){

            synchronized (this){

                allocEntity = allocRepository.findByBizTag(bizTag);

                if(allocEntity == null){
                    allocEntity = createAllocEntity(bizTag);
                }
            }

        }
        return allocEntity;
    }



    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AllocEntity createAllocEntity(String bizTag) {
        Date now = new Date();
        AllocEntity allocEntity = new AllocEntity();
        allocEntity.setBizTag(bizTag);
        allocEntity.setMaxId(BigInteger.ZERO);
        allocEntity.setStep(idServerProperties.getSegement().getStep());
        allocEntity.setCreateTime(now);
        allocEntity.setUpdateTime(now);
        allocEntity.setVersion(BigInteger.ONE.longValue());
        return  allocRepository.save(allocEntity);
    }


    /**
     * 创建segmentId
     * @param currentMaxId
     * @return
     */
    private SegmentId createSegmentId(BigInteger currentMaxId){

      return createSegmentId(currentMaxId, BigInteger.ZERO.intValue());
    }

    /**
     * 创建segmentId
     * @param currentMaxId
     * @return
     */
    private SegmentId createSegmentId(BigInteger currentMaxId, int maxLength){

        int step = idServerProperties.getSegement().getStep();

        return new SegmentId(currentMaxId.add(BigInteger.valueOf(step)).longValue(), step, maxLength);
    }

    private void enable(){
        if(!idServerProperties.getSegement().isEnable()){
            //todo 指定错误码
            throw new IdException("segment 模式不可用");
        }
    }

}
