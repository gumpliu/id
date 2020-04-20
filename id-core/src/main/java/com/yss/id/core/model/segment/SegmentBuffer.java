package com.yss.id.core.model.segment;

import com.alibaba.fastjson.JSON;
import com.yss.id.core.constans.Constants;
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

        return String.valueOf(getCurrent().getValue().incrementAndGet());
    }

    @Override
    public boolean switchBufer() {
        //todo BigInteger  BigDecimal 超长计算
        boolean isFiexdSwitch = true;
        Segment currentBuffer = getCurrent();
        if(maxLength > 0){
            isFiexdSwitch = BigDecimal.valueOf(currentBuffer.getValue().get()).
                    compareTo(BigDecimal.valueOf(MathUtil.maxValue(currentBuffer.getBuffer().getKey(), maxLength))) >= 0;
        }
        return BigDecimal.valueOf(currentBuffer.getValue().get()).compareTo(BigDecimal.valueOf(currentBuffer.getMax())) >= 0 && isFiexdSwitch;
    }

    @Override
    public boolean isloadNextBuffer(Segment currentBuffer, Segment nextBuffer) {

        //已经获取下一缓存信息
        if(nextBuffer != null && nextBuffer.getMax() > currentBuffer.getMax()){
            return false;
        }

        long initValue = currentBuffer.getMax() - currentBuffer.getStep();

        BigDecimal currentThreshold = BigDecimal.valueOf(currentBuffer.getValue().get())
                .subtract(BigDecimal.valueOf(initValue))
                .divide(BigDecimal.valueOf(currentBuffer.getStep()), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return currentThreshold.intValue() > Constants.ID_THRESHOLDVALUE;
    }


    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength == null ? 0 : maxLength;
    }

}
