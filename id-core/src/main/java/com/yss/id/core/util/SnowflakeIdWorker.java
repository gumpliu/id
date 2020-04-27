package com.yss.id.core.util;

import com.yss.id.core.constans.IDFormatEnum;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
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

    private ArrayBlockingQueue<String> cache;

    private int casheSize;

    /**
     * 开始时间戳
     */
    private final long startTime = 1420041600000L;
    /**
     * 机器id所占的位数
     */
    private final long workIdBits = 10L;


    /**
     * 二进制中，负数采用其绝对值的值得补码得到
     * long型的-1 的值就32个1
     */
    private final long maxWorkerId = -1L ^ (-1L << workIdBits);

    /**
     * 12位的序列，表示1个毫秒内可生成 2的12次幂个数据，即4096个数据
     */
    private final long sequenceBits = 12L;

    /**
     * 时间戳存放的位置应该是从22位开始的，左移22位
     */
    private final long timestampLeftShift = sequenceBits + workIdBits;
//    /**
//     * 4095 生成序列的最大值
//     */
//    private final long maxSequence = -1 ^ (-1 << sequenceBits);
    /**
     * 机器码id 小于31
     */
    private long workerId;

    private String workerIdStr;


    /**
     * 毫秒内计数(0-4095)
     */
    private volatile AtomicLong sequence = new AtomicLong(0);

    private Map<String, String> formatMap = new ConcurrentHashMap<>();

    /**
     * 上一次生成id的时间戳
     */
    private volatile long lastTimeStamp = -1L;

    private IDFormatEnum format;

    public SnowflakeIdWorker(IDFormatEnum format, int casheSize){
        this.format = format;
        this.casheSize = casheSize;
        cache = new ArrayBlockingQueue<String>(casheSize);
    }

    /**
     * 初始化并配置机器码id和数据id
     *
     * @param workerId
     */
    public void init(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't greater than %d or less than 0", maxWorkerId));
        }

        this.workerId = workerId;
        if(workerId < 10){
            this.workerIdStr = "0"+workerId;
        }else{
            this.workerIdStr = workerId + "";
        }
    }

    public String nextId(){
        synchronized (this){
            while (true){
                String id = cache.poll();
                if(id != null) { return id; }
                batchCache(casheSize);
            }
        }
    }


    private void batchCache(int size) {

        cache.clear();

        for (int i = 0; i < size; i++) {
            if (!cache.offer(genNextId())) {
                break;
            }
        }
    }


    /**
     * 生成主键id，理论上应该在调用了init方法之后，调用生成的方式是有效的
     * 不然所有的id都默认是按照机器码和数据id都是0的情况处理
     *
     * @return 8个字节的长整型
     */
    private String genNextId() {

        long timeStamp = genTimeStamp();

        long compareTimeStamp =  compareTime(timeStamp);

        long compareLastTimeStamp = compareTime(this.lastTimeStamp);

        /**表示系统的时间修改了*/
        if (compareTimeStamp < compareLastTimeStamp) {
            throw new RuntimeException(String.format("System clock moved;currentTimeStamp %d,lastTimeStamp = %d", timeStamp, this.lastTimeStamp));
        }

        if (compareTimeStamp == compareLastTimeStamp) {
            /**查看序列是否溢出*/

            if (sequence.incrementAndGet() >= format.getMaxSequence()) {
                /**当出现溢出的时候，阻塞到下一个毫秒或秒*/
                this.toNextTime(compareLastTimeStamp);
            }

        } else {  /**此时表示时间戳跟最后的时间戳不一致,需要重置序列*/
            removeKey();
            this.sequence.set(0);
        }
        this.lastTimeStamp = genTimeStamp();

        return getId();
    }

    private String getId(){

        if(IDFormatEnum.ID_FORMAT_NORMAL == format){
            return String.valueOf (((lastTimeStamp - startTime) << timestampLeftShift)
                    | (workerId << sequenceBits)
                    | sequence.get());
        }

        //使用currentTime时重复，因为lastTimeStamp与currentTime 时间可能不同步
        //不能使用System.currentTimeMillis()，可能与下一秒或毫秒id重复，只能使用lastTimeStamp，秒 * 1000

        return getFormatValue() + workerIdStr +  MathUtil.appendZero(String.valueOf(sequence), format.getLength());
    }

    /**
     * 删除Map中key
     */
    private void removeKey(){
        if(format != IDFormatEnum.ID_FORMAT_NORMAL){
            formatMap.remove(compareTime(this.lastTimeStamp)+"");
        }
    }

    /**
     * 获取时间格式
     * @return
     */
    private String getFormatValue(){
        String key = compareTime(lastTimeStamp)+"";
        String dateTime;
        if(formatMap.containsKey(key)){
            dateTime =  formatMap.get(key);
        }else {
            dateTime  = DateUtil.dateToString(lastTimeStamp, format.getFormat());
            formatMap.put(key, dateTime);
        }

        return dateTime;
    }

    /**
     * 获取时间比较时间，秒 除以1000
     * @param time
     * @return
     */
    private long compareTime(long time){
        if(format == IDFormatEnum.ID_FORMAT_SECOND
            || format == IDFormatEnum.ID_FORMAT_SHOT_YEAR_SECOND){
            return time / 1000;
        }

        return time;
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
    private void toNextTime(long lastTimeStamp) {
        long timeStamp = compareTime(genTimeStamp());

        while (timeStamp <= lastTimeStamp) {
            timeStamp = compareTime(genTimeStamp());
        }
        removeKey();
        this.sequence.set(0L);
    }


//    public static String getDateFormat(IDFormatEnum idFormatEnum){
//
//        switch (idFormatEnum){
//            case ID_FORMAT_SHOT_YEAR_SECOND:
//                return ID_FORMAT_SHOT_YEAR_SECOND;
//            case ID_FORMAT_SECOND:
//                return ID_FORMAT_SECOND;
//            case ID_FORMAT_SHOT_YEAR_MILLISECOND:
//                return ID_FORMAT_SHOT_YEAR_MILLISECOND;
//            case ID_FORMAT_MILLISECOND:
//                return ID_FORMAT_MILLISECOND;
//        }
//        return "";
//    }
    //--------------------------test--------------------------------------
    public static void main(String[] args) {

        SnowflakeIdWorker worker = new SnowflakeIdWorker(IDFormatEnum.ID_FORMAT_SECOND, 100);
        worker.init(3);
        Map<String, String> formatMap = new ConcurrentHashMap<>();
        long startTime = System.currentTimeMillis();
//        for (int i = 0; i < 200; i++) {
//
//            System.out.println(worker.nextId());
//        }
////
//        long time = System.currentTimeMillis() -startTime;
//        System.out.println("====time=" + time);


//
        final Hashtable<String, String> ids = new Hashtable<String, String>();

        final int idMax = 500000;
        final CountDownLatch cntdown = new CountDownLatch(4);
        List<String> idc = new ArrayList<>();

//        long startTime=System.currentTimeMillis();   //获取开始时间
        Thread t1 = new Thread(new Runnable() {

            public void run() {

                for (int i = 0; i < idMax; i++) {
                    String id = worker.nextId();

                    ids.put(id, i + "");
                }
                System.out.println("ok1");
                cntdown.countDown();
            }
        });

        Thread t2 = new Thread(new Runnable() {

            public void run() {

                for (int i = 0; i < idMax; i++) {
                    String id = worker.nextId();
                    ids.put(id, i + "");
                }
                System.out.println("ok2");
                cntdown.countDown();
            }
        });

        Thread t3 = new Thread(new Runnable() {

            public void run() {

                for (int i = 0; i < idMax; i++) {
                    String id = worker.nextId();
                    ids.put(id, i + "");
                }
                System.out.println("ok3");
                cntdown.countDown();
            }
        });
        Thread t4 = new Thread(new Runnable() {

            public void run() {

                for (int i = 0; i < idMax; i++) {
                    String id = worker.nextId();
                    System.out.println(id);
                    ids.put(id, i + "");
                }
                System.out.println("ok4");
                cntdown.countDown();
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        int i = 0;

        System.out.println("检查id是否重复....");
        try {
            cntdown.await();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        if (ids.size() < 4 * idMax) {
            System.err.println("id生成重复");
        } else {
            System.out.println("无重复id， size＝" + ids.size());
        }
        System.out.println("重复id， size＝" + idc.toString());



        long endTime=System.currentTimeMillis(); //获取结束时间

        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");



    }

}