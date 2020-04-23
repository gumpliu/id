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
    public String nextId() {
        if(maxLength > 0){
            synchronized (this){
                return getCurrent().getValue().incrementAndGet() + "";
            }
        }else {
            return getCurrent().getValue().incrementAndGet() + "";
        }
    }

    @Override
    public boolean isInitBuffer(String nextId){
        if(maxLength <= 0){
            return false;
        }
        return Long.parseLong(nextId) > MathUtil.maxValue(getKey(), maxLength);
    }


    @Override
    public boolean switchBufer(String nextId) {
        return getMaxId().longValue() < Long.parseLong(nextId);
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
    public boolean isloadNextBuffer(String nextId) {
        return Long.parseLong(nextId) > getCurrent().getLoadingValue();
    }


    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength == null ? 0 : maxLength;
    }

}
