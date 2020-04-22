package com.yss.id.core.model.snowflake;

import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.model.BaseBuffer;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Description: 雪花模式返回结构
 * @Author gumpLiu
 * @Date 2020-03-30
 * @Version V1.0
 **/
public class SnowflakeBuffer extends BaseBuffer<Snowflake> {

    private IDFormatEnum format;

    private int loadingValue;

    public SnowflakeBuffer(){
        buffers =  new Snowflake[]{new Snowflake(this), null};
    }

    @Override
    public String nextId() {
        return getCurrent().getQueue().remove();
    }

    @Override
    public boolean switchBufer(String nextId) {
        return getCurrent().getQueue().isEmpty();
    }

    @Override
    public boolean isCurrentEmpty() {
        return false;
    }


    @Override
    public BigDecimal getMaxId() {
        return null;
    }

    @Override
    public boolean isloadNextBuffer(String nextId) {

        return getCurrent().getQueue().size() <= loadingValue;
    }

    public IDFormatEnum getFormat() {
        return format;
    }


    public void setFormat(IDFormatEnum format) {
        this.format = format;
    }

    public int getLoadingValue() {
        return loadingValue;
    }

    public void setLoadingValue(int loadingValue) {
        this.loadingValue = loadingValue;
    }
}
