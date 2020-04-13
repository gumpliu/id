package com.yss.id.core.model.snowflake;

import com.yss.id.core.constans.Constants;
import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.model.BaseBuffer;

import java.math.BigDecimal;

/**
 * @Description: 雪花模式返回结构
 * @Author gumpLiu
 * @Date 2020-03-30
 * @Version V1.0
 **/
public class SnowflakeBuffer extends BaseBuffer<Snowflake> {

    private IDFormatEnum format;

    public SnowflakeBuffer(){
        buffers =  new Snowflake[]{new Snowflake(this), null};
    }

    @Override
    public String nextId() {
        return getCurrent().getQueue().remove();
    }

    @Override
    public boolean switchBufer() {
        return getCurrent().getQueue().isEmpty();
    }

    @Override
    public boolean isloadNextBuffer(Snowflake currentBuffer, Snowflake nextBuffer) {

        //已经获取下一缓存信息
        if(nextBuffer != null && !nextBuffer.getQueue().isEmpty()){
            return false;
        }

        BigDecimal currentThreshold = BigDecimal.valueOf(currentBuffer.getStep())
                .subtract(BigDecimal.valueOf(currentBuffer.getQueue().size()))
                .divide(BigDecimal.valueOf(currentBuffer.getStep()), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return currentThreshold.intValue() > Constants.ID_THRESHOLDVALUE;
    }

    public IDFormatEnum getFormat() {
        return format;
    }

    public void setFormat(IDFormatEnum format) {
        this.format = format;
    }
}
