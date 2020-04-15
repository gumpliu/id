package com.yss.id.core.generator;

import com.alibaba.fastjson.JSON;
import com.yss.id.core.constans.Constants;
import com.yss.id.core.model.BaseBuffer;
import com.yss.id.core.service.IdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Description: id 生成器基础实现，统一实现双缓存
 * @Author gumpLiu
 * @Date 2020-03-26
 * @Version V1.0
 **/
public abstract class AbstractIdGenerator<T> {

    protected Map<String, BaseBuffer<T>> baseMap = new ConcurrentHashMap<String, BaseBuffer<T>>();

    private static ExecutorService executor = Executors.newFixedThreadPool(Constants.THREAD_CORE);

    protected IdService idService;

    private static final Logger logger = LoggerFactory.getLogger(AbstractIdGenerator.class);

    public AbstractIdGenerator(IdService idService){
        this.idService = idService;
    }

    /**
     * 获取 nextId
     * @param bizTag
     * @return
     */
    public String nextId(String bizTag){
        logger.info(" Get id start, param bizTag = {}.", bizTag);

        BaseBuffer<T> baseBuffer = getBuffer(bizTag);

        String nextId = "";

        synchronized (baseBuffer){
            //获取下一缓存
            loadNextBuffer(bizTag);

            while (baseBuffer.getThreadRunning().get()){
                waitAndSleep(baseBuffer);
            }

            nextId = baseBuffer.nextId();

            //nextId 等于maxId时，切换缓存
            if(baseBuffer.switchBufer()){
                logger.info(" Swith buffer ...., bizTag = {}.", bizTag);
                baseBuffer.getBuffers()[baseBuffer.getCurrentPos()] = null;
                baseBuffer.switchPos();
                baseBuffer.setAlreadyLoadBuffer(false);
            }
        }
        logger.info(" Get id end, return nextId is {}.", nextId);

        return nextId;
    }

    /**
     * 获取缓存
     *
     * @param bizTag 业务类型
     *               segment 对应业务类型
     *               snowflake 使用模式key，因为雪花模式只缓存一种全局使用
     * @return
     */
    public BaseBuffer getBuffer(String bizTag){

        BaseBuffer baseBuffer = baseMap.get(bizTag);

        if(baseBuffer == null){
            logger.info(" Init BaseBuffer start, param bizTag = {}.", bizTag);

            baseBuffer = initBaseBuffer(bizTag);

            if(logger.isDebugEnabled()){
                logger.info(" Init BaseBuffer end, baseBuffer = {}.", JSON.toJSONString(baseBuffer));
            }

        }

        return baseBuffer;
    }

    /**
     * 初始化SegmentBuffer，并加载当前segment
     *
     * @param bizTag
     */
    protected BaseBuffer initBaseBuffer(String bizTag){

        BaseBuffer baseBuffer = baseMap.get(bizTag);

        if(baseMap.get(bizTag) == null){
            synchronized (baseMap){
                if(baseMap.get(bizTag) != null){
                    return baseMap.get(bizTag);
                }
                baseBuffer =  createBaseBuffer(bizTag);

                baseMap.put(bizTag, baseBuffer);
            }
        }
        return baseBuffer;
    }

    /**
     * 加载下一个缓存，超过设置阈值 获取下一个缓存
     * todo 开启线程运行
     *
     * @param bizTag
     */
    protected void loadNextBuffer(String bizTag){
        BaseBuffer baseBuffer  = baseMap.get(bizTag);

        T currentBuffer = (T) baseBuffer.getCurrent();

        T nextBuffer = (T) baseBuffer.getBuffers()[baseBuffer.nextPos()];

        //是否需要获取下一缓存，维护nextReady状态

        if(!baseBuffer.isAlreadyLoadBuffer()
                && baseBuffer.isloadNextBuffer(currentBuffer, nextBuffer)
                && baseBuffer.getThreadRunning().compareAndSet(false, true)){

            logger.info("load next buffer start... , bizTag={}.", bizTag);

            executor.execute(()->{
                if(!baseBuffer.isAlreadyLoadBuffer()){
                    //远程调用服务获取id集合，并添加至缓存中
                    try {
                        romoteLoadNextBuffer(bizTag);
                        baseBuffer.setAlreadyLoadBuffer(true);

                        if(logger.isDebugEnabled()){
                            logger.debug("load next buffer end ... ,bizTag={}, baseBuffer={}.", bizTag, JSON.toJSONString(baseBuffer));
                        }else{
                            logger.info("load next buffer end ... , bizTag={}.", bizTag);
                        }

                    }finally {
                        baseBuffer.getThreadRunning().set(false);
                    }

                }else{
                    logger.info("already load next buffer, bizTag={}.", bizTag);
                }
            });

        }

    }

    private void waitAndSleep(BaseBuffer buffer) {
        int roll = 0;
        while (buffer.getThreadRunning().get()) {
            roll += 1;
            if(roll > 10000) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                    break;
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * 初始化baseBuffer
     * @param bizTag
     * @return
     */
    protected abstract BaseBuffer createBaseBuffer(String bizTag);


    /**
     * 远程获取缓存内容
     *
     * @param bizTag
     */
    protected abstract void romoteLoadNextBuffer(String bizTag);

}
