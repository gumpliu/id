package com.yss.id.client.server.domain.segment.model;

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
}
