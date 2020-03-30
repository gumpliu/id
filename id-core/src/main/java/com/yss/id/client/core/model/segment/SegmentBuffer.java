package com.yss.id.client.core.model.segment;

import com.yss.id.client.core.model.BaseBuffer;

/**
 * 号段模式 双缓存
 */
public class SegmentBuffer extends BaseBuffer<Segment> {

    public SegmentBuffer() {
        buffers = new Segment[]{new Segment(this), null};
    }
}
