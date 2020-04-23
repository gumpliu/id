package com.yss.id.core.util;

import com.yss.id.core.constans.IDFormatEnum;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 生成分布式系统唯一的主键id
 * string类型
 * Snowflake 的结构如下
 * 0-00000000 00000000 00000000 00000000 00000000 00000000 - 00000 - 0000 -000000000000
 * 第一位是符号为，41位表示当前时间戳跟定义好的时间戳的差值，5位
 * 41位的表示(1L << 41) / (1000L * 3600 * 24 * 365L) == 69年;
 * 指定10位的机器位，可以部署1024台机器
 * 所有的数据加起来就是64位，正好是一个Long型数据
 */
public class SnowflakeIdWorker {
    /**
     * 开始时间戳
     */
    private final long startTime = 1420041600000L;
    /**
     * 机器id所占的位数
     */
    private final long workIdBits = 5L;
    /**
     * 数据id所占的位数
     */
    private final long dataIdBits = 5L;
    /**
     * 二进制中，负数采用其绝对值的值得补码得到
     * long型的-1 的值就32个1
     */
    private final long maxWorkerId = -1L ^ (-1L << workIdBits);
    /**
     * 同上
     */
    private final long maxDataGenID = -1L ^ (-1L << dataIdBits);
    /**
     * 12位的序列，表示1个毫秒内可生成 2的12次幂个数据，即4096个数据
     */
    private final long sequenceBits = 12L;
    /**
     * 数据id存放的位置应该是向左移动12位序列值和机器码
     */
    private final long dataShiftBits = sequenceBits + workIdBits;
    /**
     * 时间戳存放的位置应该是从22位开始的，左移22位
     */
    private final long timestampLeftShift = dataShiftBits + dataIdBits;
//    /**
//     * 4095 生成序列的最大值
//     */
//    private final long maxSequence = -1 ^ (-1 << sequenceBits);
    /**
     * 机器码id 小于31
     */
    private long worderId;
    /**
     * 数据中心id 小于31
     */
    private long dataId;
    /**
     * 毫秒内计数(0-4095)
     */
    private volatile AtomicLong sequence = new AtomicLong(0);
    /**
     * 上一次生成id的时间戳
     */
    private volatile long lastTimeStamp = -1L;

    private IDFormatEnum format;

    public SnowflakeIdWorker(IDFormatEnum format){
        this.format = format;
    }

    /**
     * 初始化并配置机器码id和数据id
     *
     * @param workerId 0-31
     * @param dataId   0-31
     */
    public void init(long workerId, long dataId) {
        if (workerId > maxWorkerId || worderId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't greater than %d or less than 0", maxWorkerId));
        }
        if (dataId > maxDataGenID || dataId < 0) {
            throw new IllegalArgumentException(String.format("data Id can't greater than %d or less than 0", maxDataGenID));
        }
        this.worderId = workerId;
        this.dataId = dataId;
    }

    /**
     * 生成主键id，理论上应该在调用了init方法之后，调用生成的方式是有效的
     * 不然所有的id都默认是按照机器码和数据id都是0的情况处理
     *
     * @return 8个字节的长整型
     */
    public  String genNextId() {
        long timeStamp = genTimeStamp();
        /**表示系统的时间修改了*/
        if (timeStamp < this.lastTimeStamp) {
            throw new RuntimeException(String.format("System clock moved;currentTimeStamp %d,lastTimeStamp = %d", timeStamp, this.lastTimeStamp));
        }
        if (timeStamp == this.lastTimeStamp) {
            /**查看序列是否溢出*/
            if ((sequence.incrementAndGet() & format.getMaxSequence()) == 0) {
                /**当出现溢出的时候，阻塞到下一个毫秒*/
                timeStamp = this.toNextMillis(this.lastTimeStamp);
            }
        } else {  /**此时表示时间戳跟最后的时间戳不一致,需要重置序列*/
            this.sequence.set(0);
        }
        this.lastTimeStamp = timeStamp;

        return getId();
    }


    private String getId(){

        if(IDFormatEnum.ID_FORMAT_NORMAL == format){
            return String.valueOf (((lastTimeStamp - startTime) << timestampLeftShift)
                    | (dataId << dataShiftBits)
                    | (worderId << sequenceBits)
                    | sequence.get());
        }

        String date = DateUtil.dateToString(lastTimeStamp, format.getFormat());

        return date + worderId +  MathUtil.appendZero(String.valueOf(sequence), format.getLength());

    }

    /**
     * 生成当前时间戳，单独写一个方法的原因是，若之后的时候修改扩展，不影响之前的业务，
     * 只在这个方法里面处理我们需要的数据
     *
     * @return
     */
    private long genTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimeStamp 上传生成id的时间戳
     * @return
     */
    private long toNextMillis(long lastTimeStamp) {
        long timeStamp = this.genTimeStamp();
        while (timeStamp <= lastTimeStamp) {
            System.out.println("========");
            timeStamp = this.genTimeStamp();
        }
        return timeStamp;
    }

    //--------------------------test--------------------------------------
    public static void main(String[] args) {
        SnowflakeIdWorker worker = new SnowflakeIdWorker(IDFormatEnum.ID_FORMAT_SHOT_YEAR_SECOND);
        /**第一次使用的时候希望初始化*/
        worker.init(30, 30);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            worker.genNextId();
        }

        long time = System.currentTimeMillis() -startTime;
        System.out.println("====time=" + time);
    }
}