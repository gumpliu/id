package com.yss.id.client.core.model;

/**
 * @Description: segment 信息
 * @Author gumpLiu
 * @Date 2020-03-24
 * @Version V1.0
 **/
public class SegmentId {

    /** 当前最大值 **/
    private Long maxId;

    /** 步长 **/
    private int step;

    private int maxLength;

    public SegmentId(){}

    public SegmentId(Long maxId, int step){
        this(maxId, step, 0);
    }

    public SegmentId(Long maxId, int step, int maxLength){
        this.maxId = maxId;
        this.step = step;
        this.maxLength = maxLength;
    }


    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
