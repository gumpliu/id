package com.yss.id.client.core.model.segment;

import com.yss.id.client.core.model.BaseBuffer;

/**
 * 号段模式 双缓存
 */
public class SegmentBuffer extends BaseBuffer {
    private Segment[] segments; //双buffer

    public SegmentBuffer() {
        segments = new Segment[]{new Segment(this), null};
    }

    public Segment[] getSegments() {
        return segments;
    }

    public Segment getCurrent() {
        return segments[getCurrentPos()];
    }

}
