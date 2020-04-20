package com.yss.id.server.domain.segment.service.impl;

import com.yss.id.core.model.SegmentId;
import com.yss.id.core.util.MathUtil;
import com.yss.id.server.config.IdServerProperties;
import com.yss.id.server.domain.segment.entity.AllocEntity;
import com.yss.id.server.domain.segment.repository.AllocRepository;
import com.yss.id.server.domain.segment.service.SegmentIdService;
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
        int step = getStep();

        while (true){
            AllocEntity allocEntity = getAllocEntity(bizTag);

            BigInteger newMaxId = BigInteger.valueOf(step).add(allocEntity.getMaxId());

            int updateNum = allocRepository.updateByBizTag(bizTag, newMaxId, step, allocEntity.getVersion());

            if(updateNum == 1){
                return createSegmentId(allocEntity.getMaxId());
            }
        }
    }

    @Override
    @Transactional
    public SegmentId getSegment(String bizTag, int length) {

        long maxValue  = MathUtil.maxValue(bizTag, length);

        int step = getStep();

        while (true){
            AllocEntity allocEntity = getAllocEntity(bizTag);

            BigInteger maxId = allocEntity.getMaxId();

            int updateNum;
            if(MathUtil.greaterThanEqual(allocEntity.getMaxId().longValue(), maxValue)){
                //初始化segment
                updateNum = allocRepository.initSegmentByBizTag(bizTag, BigInteger.valueOf(step), step, allocEntity.getVersion());

                maxId = BigInteger.ZERO;
            }else{
                BigInteger newMaxId = BigInteger.valueOf(step).add(allocEntity.getMaxId());
                updateNum = allocRepository.updateByBizTag(bizTag, newMaxId,step, allocEntity.getVersion());
            }

            if(updateNum == 1){
                return createSegmentId(maxId, length);
            }
        }


    }

    @Override
    @Transactional
    public SegmentId initSegment(String bizTag) {

        int step = getStep();

        while (true){
            AllocEntity allocEntity = getAllocEntity(bizTag);

            int updateNum = allocRepository.initSegmentByBizTag(bizTag, BigInteger.valueOf(step) , step, allocEntity.getVersion());

            if(updateNum == 1){
                return createSegmentId(BigInteger.ZERO);
            }
        }
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

    private Integer getStep(){
        return idServerProperties.getSegement().getStep();
    }

}
