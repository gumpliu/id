package com.yss.id.client.core.generator.segment;

import com.yss.id.client.core.generator.AbstractIdGenerator;
import com.yss.id.client.core.model.BaseBuffer;
import com.yss.id.client.core.model.SegmentId;
import com.yss.id.client.core.model.segment.Segment;
import com.yss.id.client.core.model.segment.SegmentBuffer;
import com.yss.id.client.core.service.IdService;
import com.yss.id.client.core.util.MathUtil;

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
        //todo 隐式为bizTag -> length
        putBizTagLen(bizTag, length);

        SegmentBuffer segmentBuffer = (SegmentBuffer) getBuffer(bizTag);

        synchronized (segmentBuffer){
            Segment segment = segmentBuffer.getCurrent();

            //nextId = currentId + 1，currentId初始默认为0
            segment.getValue().incrementAndGet();

            //nextId超过位数最大值，segment重新初始化
            if(segment.getValue().get() > MathUtil.maxValue(bizTag, length)){
                SegmentId segmentId = idService.initSegmentId(bizTag);
                segment.setMax(segmentId.getMaxId());
                segment.setStep(segmentId.getStep());
                AtomicLong currentId = new AtomicLong(segment.getMax() - segment.getStep());
                segment.setValue(currentId);
                //另一缓存设置为空
                if(segmentBuffer.getBuffers()[segmentBuffer.nextPos()] != null){
                    segmentBuffer.getBuffers()[segmentBuffer.nextPos()] = null;
                }

                //重新计算nextId
                segment.getValue().incrementAndGet();
            }

            //获取下一缓存
            loadNextBuffer(bizTag);
            //nextId 等于maxId时，切换缓存
            if(segmentBuffer.switchBufer(segment)){
                segmentBuffer.switchPos();
                segmentBuffer.setAlreadyLoadBuffer(false);
            }

            return MathUtil.appendZero(segment.getValue().toString(), length);
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
    protected void romoteLoadNextBuffer(String bizTag) {
        //远程调用服务获取segment
        SegmentBuffer segmentBuffer = (SegmentBuffer) baseMap.get(bizTag);

        Segment segment = getSegmentId(segmentBuffer);

        segmentBuffer.getBuffers()[segmentBuffer.nextPos()] = segment;
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

        Segment segment = new Segment(segmentBuffer);
        segment.setMax(segmentId.getMaxId());
        segment.setStep(segmentId.getStep());
        AtomicLong currentId = new AtomicLong(segment.getMax() - segment.getStep());
        segment.setValue(currentId);

        return segment;
    }


    private void putBizTagLen(String bizTag, int length){
        if(!bizTagLenMap.containsKey(bizTag)){
            bizTagLenMap.put(bizTag, length);
        }
    }
}
