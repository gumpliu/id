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
    public synchronized String nextId() {
       return String.valueOf(getCurrent().getValue().incrementAndGet());
    }

    @Override
    public boolean isInitBuffer(String nextId){
        if(maxLength <= 0){
            return false;
        }
        return strToDecimal(nextId).compareTo(BigDecimal.valueOf(MathUtil.maxValue(getKey(), maxLength))) == 1;
    }


    @Override
    public boolean switchBufer(String nextId) {
        return getMaxId().compareTo(strToDecimal(nextId)) < 0;
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

        return strToDecimal(nextId).compareTo(getCurrent().getLoadingValue()) > 0;
    }


    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength == null ? 0 : maxLength;
    }

}
