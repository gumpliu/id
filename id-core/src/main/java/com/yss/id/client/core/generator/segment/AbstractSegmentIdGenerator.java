package com.yss.id.client.core.generator.segment;

import com.yss.id.client.core.Constants;
import com.yss.id.client.core.exception.IdException;
import com.yss.id.client.core.generator.AbstractIdGenerator;
import com.yss.id.client.core.model.BaseBuffer;
import com.yss.id.client.core.model.SegmentId;
import com.yss.id.client.core.model.segment.Segment;
import com.yss.id.client.core.model.segment.SegmentBuffer;
import com.yss.id.client.core.service.IdService;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 号段模式实现，直接从id-server获取id
 * @Author gumpLiu
 * @Date 2020-03-26
 * @Version V1.0
 **/
public abstract class AbstractSegmentIdGenerator extends AbstractIdGenerator<Segment> implements IdSegmentGenerator {

    private Map<String, Long> maxValueMap = new ConcurrentHashMap<String, Long>();

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    public AbstractSegmentIdGenerator(IdService idService) {
        super(idService);
    }

    /**
     * 获取 nextId
     * @param bizTag
     * @return
     */
    public String nextId(String bizTag){

        SegmentBuffer segmentBuffer = (SegmentBuffer) getBuffer(bizTag);

        Segment segment = segmentBuffer.getCurrent();

        //nextId = currentId + 1，currentId初始默认为0
        long nextId = segment.getValue().incrementAndGet();

        //nextId 等于maxId时，切换缓存
        if(nextId == segment.getMax()){
            segmentBuffer.switchPos();
        }

        //获取下一缓存
        loadNextBuffer(bizTag);

        return BigInteger.valueOf(nextId).toString();
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

        SegmentBuffer segmentBuffer = (SegmentBuffer) getBuffer(bizTag);

        Segment segment = segmentBuffer.getCurrent();


        //nextId = currentId + 1，currentId初始默认为0
        long nextId = segment.getValue().incrementAndGet();

        //nextId超过位数最大值，segment重新初始化
        if(nextId > maxValue(bizTag, length)){
            SegmentId segmentId = idService.initSegmentId(bizTag);
            segment.setMax(segmentId.getMaxId());
            segment.setStep(segmentId.getStep());

            //另一缓存设置为空
            if(segmentBuffer.getBuffers()[segmentBuffer.nextPos()] != null){
                segmentBuffer.getBuffers()[segmentBuffer.nextPos()] = null;
            }

            //重新计算nextId
            nextId = segment.getValue().getAndAdd(1L);
        }

        if(nextId == segment.getMax()){
            segmentBuffer.switchPos();
        }

        //获取下一缓存
        loadNextBuffer(bizTag);

        return appendZero(segment.getValue().toString(), length);
    }

    @Override
    public BaseBuffer createBaseBuffer(String bizTag) {

        SegmentBuffer segmentBuffer = new SegmentBuffer();

        baseMap.put(bizTag, segmentBuffer);

        //远程调用服务获取segment
        SegmentId segmentId = idService.getSegmentId(bizTag);
        Segment segment = segmentBuffer.getCurrent();
        segment.setMax(segmentId.getMaxId());
        segment.setStep(segmentId.getStep());

        return segmentBuffer;
    }

    @Override
    public boolean isloadNextBuffer(Segment currentBuffer, Segment nextBuffer) {

        //已经获取下一缓存信息
        if(nextBuffer != null && nextBuffer.getMax() > currentBuffer.getMax()){
            return false;
        }

        long initValue = currentBuffer.getMax() - currentBuffer.getStep();

        int currentThreshold = (int)((currentBuffer.getValue().get() - initValue) / currentBuffer.getStep() * 100);

        return currentThreshold >= Constants.ID_THRESHOLDVALUE;

    }

    @Override
    public void romoteLoadNextBuffer(String bizTag, BaseBuffer baseBuffer) {
        //远程调用服务获取segment
        SegmentBuffer segmentBuffer = (SegmentBuffer) baseBuffer;
        SegmentId segmentId = idService.getSegmentId(bizTag);
        Segment segment = new Segment(segmentBuffer);
        segment.setMax(segmentId.getMaxId());
        segment.setStep(segmentId.getStep());
        segmentBuffer.getBuffers()[segmentBuffer.nextPos()] = segment;
    }

    /**
     * 获取位数为length的最大值
     * @param length
     * @return
     */
    private long maxValue(String bizTag, int length){

        String maxValueKey = bizTag + length;
        Long maxValue =  maxValueMap.get(maxValueKey);

        if(maxValue == null){

            StringBuffer initValue = new StringBuffer();
            for(int i = 0; i < length; i++){
                initValue.append(9);
            }
            maxValueMap.put(maxValueKey, Long.parseLong(initValue.toString()));
            maxValue = maxValueMap.get(maxValueKey);
        }

        return maxValue;
    }

    /**
     * 拼接0
     * @param id
     * @param length
     * @return
     */
    private String appendZero(String id, int length){

        if(id.length() == length){
            return id;
        }
        int differenceValue = length - id.length();
        if(differenceValue > 0 ){
            StringBuffer initValue = new StringBuffer();
            for(int i = 0; i < differenceValue; i++ ){
                initValue.append("0");
            }
            return id + initValue.toString();
        }else{
            //todo 定义自己异常
            throw new IdException("id length is error !!");
        }
    }


}
