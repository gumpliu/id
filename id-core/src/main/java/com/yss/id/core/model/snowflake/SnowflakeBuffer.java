package com.yss.id.core.model.snowflake;

import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.core.model.BaseBuffer;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;

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

    //去掉synchronized，在nextId加一个锁，性能提升
    @Override
    public String nextId() {
        if(isCurrentEmpty()){
            return "";
        }
        return getCurrent().getQueue().remove();
    }

    @Override
    public boolean switchBufer(String nextId) {
        return StringUtils.isEmpty(nextId) || isCurrentEmpty();
    }
    //去掉synchronized，在nextId加一个锁，性能提升
    @Override
    public boolean isCurrentEmpty() {
        return getCurrent() == null
                || getCurrent().getQueue() == null
                || getCurrent().getQueue().isEmpty();
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
