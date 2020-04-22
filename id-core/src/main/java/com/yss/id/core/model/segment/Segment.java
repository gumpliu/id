package com.yss.id.core.model.segment;

import com.yss.id.core.model.BaseBuffer;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

/**
 * segment 缓存对象
 */
public class Segment {
    private AtomicLong value = new AtomicLong(0);
    private volatile long max;
    private volatile int step;
    private SegmentBuffer buffer;
    private volatile BigDecimal loadingValue; //大于等于loadingValue值时，load next buffer

    public Segment(SegmentBuffer buffer) {
        this.buffer = buffer;
    }

    public AtomicLong getValue() {
        return value;
    }

    public void setValue(AtomicLong value) {
        this.value = value;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public BaseBuffer getBuffer() {
        return buffer;
    }

    public long getIdle() {
        return this.getMax() - getValue().get();
    }

    public BigDecimal getLoadingValue() {
        return loadingValue;
    }

    public void setLoadingValue(BigDecimal loadingValue) {
        this.loadingValue = loadingValue;
    }
}
