package com.yss.id.core.generator.segment;

import com.yss.id.core.constans.Constants;
import com.yss.id.core.exception.IdException;
import com.yss.id.core.generator.AbstractIdGenerator;
import com.yss.id.core.model.BaseBuffer;
import com.yss.id.core.model.SegmentId;
import com.yss.id.core.model.segment.Segment;
import com.yss.id.core.model.segment.SegmentBuffer;
import com.yss.id.core.service.IdService;
import com.yss.id.core.util.MathUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 号段模式实现，直接从id-server获取id
 * @Author gumpLiu
 * @Date 2020-03-26
 * @Version V1.0
 **/
public class SegmentBufferIdGenerator extends AbstractIdGenerator<Segment> {

    private static final Logger logger = LoggerFactory.getLogger(SegmentBufferIdGenerator.class);

    private Map<String, Integer> bizTagLenMap = new ConcurrentHashMap<String, Integer>();

    public SegmentBufferIdGenerator(IdService idService) {
        super(idService);
    }

    /**
     * 获取 固定长度 nextId，默认为6位 位数不够前面补0
     * @param bizTag
     * @return
     */
    public String fixedLengthNextId(String bizTag){

        return fixedLengthNextId(bizTag, 6);
    }

    /**
     * 获取固定长度 nextId 位数不够前面补0，超过长度nextId初始为1
     *
     * @param bizTag 业务类型
     * @param length id长度
     * @return
     */
    public String fixedLengthNextId(String bizTag, int length){
        logger.info("Get fixed length id, bizTag={}, length={}", bizTag, length);

        if(StringUtils.isEmpty(bizTag)){
            throw new IdException("fixedLengthNextId bizTag Can not be empty ！！");
        }

        if(length <=0){
            throw new IdException("fixedLengthNextId length Must be Greater than 0 ！！");
        }

        putBizTagLen(bizTag, length);

        return MathUtil.appendZero(nextId(bizTag), length);
    }

    @Override
    public  void initBuffer(BaseBuffer baseBuffer, String nextId) {

        if(baseBuffer.isInitBuffer(nextId)){
            synchronized (baseBuffer){
                if(baseBuffer.isInitBuffer(nextId)){
                    SegmentBuffer segmentBuffer = (SegmentBuffer) baseBuffer;
                    //nextId超过位数最大值，segment重新初始化
                    String bizTag = segmentBuffer.getKey();

                    SegmentId segmentId = idService.initSegmentId(bizTag);

                    Segment segment = getSegment(segmentId, segmentBuffer);
                    //初始segmentBuffer
                    segmentBuffer.getBuffers()[segmentBuffer.getCurrentPos()] = segment;
                    segmentBuffer.setAlreadyLoadBuffer(false);
                    //另一缓存设置为空
                    if(segmentBuffer.getBuffers()[segmentBuffer.nextPos()] != null){
                        segmentBuffer.getBuffers()[segmentBuffer.nextPos()] = null;
                    }
                }
            }
        }


    }


    @Override
    public BaseBuffer createBaseBuffer(String bizTag) {

        //远程调用服务获取segment
        SegmentBuffer segmentBuffer = new SegmentBuffer();
        segmentBuffer.setKey(bizTag);
        segmentBuffer.setMaxLength(bizTagLenMap.get(bizTag));
        Segment segment = getSegmentId(segmentBuffer);

        segmentBuffer.getBuffers()[segmentBuffer.getCurrentPos()] = segment;

        return segmentBuffer;
    }

    @Override
    protected Segment romoteLoadNextBuffer(String bizTag) {

        //远程调用服务获取segment
        SegmentBuffer segmentBuffer = (SegmentBuffer) baseMap.get(bizTag);

        return getSegmentId(segmentBuffer);
    }


    /**
     * 获取segmentId
     * @param segmentBuffer
     * @return
     */
    private Segment getSegmentId(SegmentBuffer segmentBuffer){
        SegmentId segmentId;

        String bizTag = segmentBuffer.getKey();

        if(bizTagLenMap.containsKey(bizTag)){
            segmentId = idService.getSegmentId(bizTag, segmentBuffer.getMaxLength());
        }else{
            segmentId = idService.getSegmentId(bizTag);
        }

        return getSegment(segmentId, segmentBuffer);
    }

    private Segment getSegment(SegmentId segmentId, SegmentBuffer segmentBuffer){

        Segment segment = new Segment(segmentBuffer);
        segment.setMax(segmentId.getMaxId());
        segment.setStep(segmentId.getStep());
        AtomicLong currentId = new AtomicLong(segment.getMax() - segment.getStep());
        segment.setValue(currentId);
        segment.setLoadingValue(getLoadingValue(currentId.get(), segmentId.getStep()).longValue());

        return segment;
    }

    /**
     * 获取loadingValue
     * @param currentId
     * @param step
     * @return
     */
    private BigDecimal getLoadingValue(long currentId, int step){

        return BigDecimal.valueOf(step)
               .divide(BigDecimal.valueOf(Constants.ID_THRESHOLDVALUE), 0, BigDecimal.ROUND_HALF_UP)
               .add(BigDecimal.valueOf(currentId));
    }


    private void putBizTagLen(String bizTag, int length){
        if(!bizTagLenMap.containsKey(bizTag)){
            bizTagLenMap.put(bizTag, length);
        }
    }
}
