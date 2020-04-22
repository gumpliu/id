package com.yss.id.core.model.segment;

import com.yss.id.core.model.BaseBuffer;
import com.yss.id.core.util.MathUtil;

import java.math.BigDecimal;

/**
 * 号段模式 双缓存
 */
public class SegmentBuffer extends BaseBuffer<Segment> {

    private int maxLength;

    public SegmentBuffer() {
        buffers = new Segment[]{new Segment(this), null};
    }


    @Override
    public BigDecimal nextId() {
        synchronized (this){
            return BigDecimal.valueOf(getCurrent().getValue().incrementAndGet());
        }
    }

    @Override
    public boolean isInitBuffer(BigDecimal nextId){
        if(maxLength <= 0){
            return false;
        }
        return nextId.compareTo(BigDecimal.valueOf(MathUtil.maxValue(getKey(), maxLength))) == 1;
    }


    @Override
    public boolean switchBufer(BigDecimal nextId) {
        return getMaxId().compareTo(nextId) < 0;
    }

    @Override
    public boolean isCurrentEmpty() {
        return getCurrent() == null;
    }

    @Override
    public BigDecimal getMaxId() {
        return BigDecimal.valueOf(getCurrent().getMax());
    }

    @Override
    public boolean isloadNextBuffer(BigDecimal nextId) {

        return nextId.compareTo(getCurrent().getLoadingValue()) > 0;
    }


    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength == null ? 0 : maxLength;
    }

}
