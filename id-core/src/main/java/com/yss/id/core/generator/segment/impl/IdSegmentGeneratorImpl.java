package com.yss.id.core.generator.segment.impl;

import com.yss.id.core.exception.IdException;
import com.yss.id.core.generator.segment.SegmentBufferIdGenerator;
import com.yss.id.core.generator.segment.IdSegmentGenerator;
import com.yss.id.core.service.IdService;

import org.apache.commons.lang.StringUtils;

/**
 * @Description: 号段模式实现
 * @Author gumpLiu
 * @Date 2020-03-30
 * @Version V1.0
 **/
public class IdSegmentGeneratorImpl extends SegmentBufferIdGenerator implements IdSegmentGenerator {

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
        if(StringUtils.isEmpty(prefix.trim())){
            throw new IdException("getSegmentFixedLengthNextId prefix Can not be empty ！！");
        }
        
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
