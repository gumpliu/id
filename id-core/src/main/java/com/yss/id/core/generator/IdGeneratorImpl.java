package com.yss.id.core.generator;

import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.generator.segment.IdSegmentGenerator;
import com.yss.id.core.generator.snowflake.IdSnowflakeGenerator;

/**
 * id生成器
 *
 * @Author gumpLiu
 * @Date 2020-03-25
 * @Version V1.0
 **/
public class IdGeneratorImpl implements IdGenerator {

    private IdSegmentGenerator segmentGenerator;

    private IdSnowflakeGenerator snowflakeGenerator;

    public IdGeneratorImpl(IdSegmentGenerator segmentGenerator,
                           IdSnowflakeGenerator snowflakeGenerator){
        this.segmentGenerator = segmentGenerator;
        this.snowflakeGenerator = snowflakeGenerator;
    }

    @Override
    public String getSegmentNextId(String bizTag) {
        return segmentGenerator.getSegmentNextId(bizTag);
    }

    @Override
    public String getSegmentFixedLengthNextId(String bizTag) {
        return segmentGenerator.getSegmentFixedLengthNextId(bizTag);
    }

    @Override
    public String getSegmentFixedLengthNextId(String prefix, String bizTag) {
        return segmentGenerator.getSegmentFixedLengthNextId(prefix, bizTag);
    }

    @Override
    public String getSegmentFixedLengthNextId(String bizTag, int length) {
        return segmentGenerator.getSegmentFixedLengthNextId(bizTag, length);
    }

    @Override
    public String getSegmentFixedLengthNextId(String prefix, String bizTag, int length) {
        return segmentGenerator.getSegmentFixedLengthNextId(prefix, bizTag, length);
    }

    @Override
    public String getSnowflakeNextId(IDFormatEnum format) {
        return snowflakeGenerator.getSnowflakeNextId(format);
    }
}
