package com.yss.id.client.core.generator.segment.impl;

import com.yss.id.client.core.generator.segment.AbstractSegmentIdGenerator;
import com.yss.id.client.core.service.IdService;

/**
 * @Description: 号段模式实现
 * @Author gumpLiu
 * @Date 2020-03-30
 * @Version V1.0
 **/
public class IdSegmentGeneratorImpl extends AbstractSegmentIdGenerator {

    public IdSegmentGeneratorImpl(IdService idService) {
        super(idService);
    }

    @Override
    public String getSegmentNextId(String bizTag) {
        return nextId(bizTag);
    }

    @Override
    public String getSegmentFixedLengthNextId(String bizTag) {
        return fixedLengthNextId(bizTag);
    }

    @Override
    public String getSegmentFixedLengthNextId(String prefix, String bizTag) {
        return prefix + fixedLengthNextId(bizTag);
    }

    @Override
    public String getSegmentFixedLengthNextId(String bizTag, int length) {
        return fixedLengthNextId(bizTag, length);
    }

    @Override
    public String getSegmentFixedLengthNextId(String prefix, String bizTag, int length) {
        return prefix + fixedLengthNextId(bizTag, length);
    }
}
