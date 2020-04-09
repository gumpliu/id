package com.yss.id.client.core.constans;

/**
 * @Author: gumpLiu 时间格式
 * @Date: 2020-04-09 9:37
 */
public enum IDFormatEnum {
    //id返回为18位，无时间格式化 -1 ^ (-1 << 12)
    ID_FORMAT_NORMAL("normal", 4095L, 4),

    //id返回为20位，精确到秒级，秒生成id为百万级别
    ID_FORMAT_SHOT_YEAR_SECOND("yyMMddHHmmss", 999999L, 6),

    //id返回为22位，精确到秒级，秒生成id为百万级别
    ID_FORMAT_SECOND("yyyyMMddHHmmss", 999999L, 6),

    //id返回为20位，精确到秒毫秒，秒生成id为千级别
    ID_FORMAT_SHOT_YEAR_MILLISECOND("yyMMddHHmmssSSS", 999L, 3),

    //id返回为22位，精确到秒毫秒，秒生成id为千级别
    ID_FORMAT_MILLISECOND("yyyyMMddHHmmssSSS", 999L, 3);

    private String format;

    private long maxSequence;

    private int length;

    IDFormatEnum(String format, long maxSequence, int length){
        this.format = format;
        this.maxSequence = maxSequence;
        this.length = length;
    }

    public String getFormat() {
        return format;
    }

    public long getMaxSequence() {
        return maxSequence;
    }

    public int getLength() {
        return length;
    }
}
