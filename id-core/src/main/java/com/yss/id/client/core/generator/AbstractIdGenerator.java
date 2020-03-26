package com.yss.id.client.core.generator;

import com.yss.id.client.core.Constants;
import com.yss.id.client.core.exception.IdException;
import com.yss.id.client.core.service.IdService;
import com.yss.id.client.core.model.SegmentId;
import com.yss.id.client.core.model.segment.Segment;
import com.yss.id.client.core.model.segment.SegmentBuffer;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: id 生成器基础实现，统一实现双缓存
 * @Author gumpLiu
 * @Date 2020-03-26
 * @Version V1.0
 **/
public abstract class AbstractIdGenerator  implements IdGenerator {

    private Map<String, SegmentBuffer> segmentMap = new ConcurrentHashMap<String, SegmentBuffer>();

    private Map<String, Long> maxValueMap = new ConcurrentHashMap<String, Long>();

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private IdService idService;

    public AbstractIdGenerator(IdService idService){
        this.idService = idService;
    }

    /**
     * 获取 nextId
     * @param bizTag
     * @return
     */
    public String nextId(String bizTag){

        Segment segment = getCurrentSegment(bizTag);

        //nextId = currentId + 1，currentId初始默认为0
        long nextId = segment.getValue().incrementAndGet();

        if(nextId == segment.getMax()){
            segmentMap.get(bizTag).switchPos();
        }

        //获取下一缓存
        loadNextSegmentBuffer(bizTag);

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

        Segment segment = getCurrentSegment(bizTag);

        SegmentBuffer segmentBuffer = segmentMap.get(bizTag);

        //nextId = currentId + 1，currentId初始默认为0
        long nextId = segment.getValue().incrementAndGet();

        //nextId超过位数最大值，segment重新初始化
        if(nextId > maxValue(bizTag, length)){
            SegmentId segmentId = idService.initSegmentId(bizTag);
            segment.setMax(segmentId.getMaxId());
            segment.setStep(segmentId.getStep());

            //另一缓存设置为空
            if(segmentBuffer.getSegments()[segmentBuffer.nextPos()] != null){
                segmentBuffer.getSegments()[segmentBuffer.nextPos()] = null;
            }

            //重新计算nextId
            nextId = segment.getValue().getAndAdd(1L);
        }

        if(nextId == segment.getMax()){
            segmentBuffer.switchPos();
        }

        //获取下一缓存
        loadNextSegmentBuffer(bizTag);

        return appendZero(segment.getValue().toString(), length);
    }

    /**
     * 初始化SegmentBuffer，并加载当前segment
     *
     * @param bizTag
     */
    protected SegmentBuffer initSegmentBuffer(String bizTag){
        //todo 锁优化
        synchronized (this){

            if(segmentMap.get(bizTag) != null){
                return segmentMap.get(bizTag);
            }

            SegmentBuffer segmentBuffer = new SegmentBuffer();

            segmentMap.put(bizTag, segmentBuffer);

            //远程调用服务获取segment
            SegmentId segmentId = idService.getSegmentId(bizTag);
            Segment segment = segmentBuffer.getCurrent();
            segment.setMax(segmentId.getMaxId());
            segment.setStep(segmentId.getStep());

            return segmentBuffer;
        }
    }

    /**
     * 加载下一个Segment，超过设置阈值 获取下一个SegmentId
     * @param bizTag
     */
    protected void loadNextSegmentBuffer(String bizTag){

        SegmentBuffer segmentBuffer  = segmentMap.get(bizTag);

        synchronized (segmentBuffer){
            Segment currentSegment = segmentBuffer.getCurrent();

            Segment nextSegment = segmentBuffer.getSegments()[segmentBuffer.getCurrentPos()];

            //已经获取下一缓存信息
            if(nextSegment != null && nextSegment.getMax() > currentSegment.getMax()){
                return;
            }

            long initValue = currentSegment.getMax() - currentSegment.getStep();

            int currentThreshold = (int)((currentSegment.getValue().get() - initValue) / currentSegment.getStep() * 100);

            if(currentThreshold >= Constants.ID_THRESHOLDVALUE){
                //远程调用服务获取segment
                SegmentId segmentId = idService.getSegmentId(bizTag);
                Segment segment = new Segment(segmentBuffer);
                segment.setMax(segmentId.getMaxId());
                segment.setStep(segmentId.getStep());
                segmentBuffer.getSegments()[segmentBuffer.nextPos()] = segment;
            }
        }
    }

    /**
     * 获取当前segment
     *
     * @param bizTag
     * @return
     */
    private Segment getCurrentSegment(String bizTag){
        SegmentBuffer segmentBuffer = segmentMap.get(bizTag);

        if(segmentBuffer == null){
            segmentBuffer = initSegmentBuffer(bizTag);
        }

        return segmentBuffer.getCurrent();
    }

    /**
     * 获取位数为length的最大值
     * @param length
     * @return
     */
    public long maxValue(String bizTag, int length){

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
