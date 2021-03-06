package com.yss.id.core.generator;

import com.alibaba.fastjson.JSON;
import com.yss.id.core.constans.Constants;
import com.yss.id.core.exception.IdException;
import com.yss.id.core.model.BaseBuffer;
import com.yss.id.core.service.IdService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        if(bizTag == null || StringUtils.isEmpty(bizTag.trim())){
            throw new IdException("nextId bizTag Can not be empty ！！");
        }
        BaseBuffer<T> baseBuffer = getBuffer(bizTag);

        synchronized (baseBuffer){
             while (true){
                 if(baseBuffer.isCurrentEmpty()){
                    loadCurrent(baseBuffer, BigDecimal.ZERO.toString());
                    continue;
                 }
                 String nextId =  baseBuffer.nextId();

                 //id是否可以使用
                 if(baseBuffer.switchBufer(nextId) || baseBuffer.isInitBuffer(nextId)){
                     loadCurrent(baseBuffer, nextId);
                 }else{
                     //判断是否需要获取下缓存
                     loadNextBuffer(baseBuffer, nextId);
                     return nextId.toString();
                 }
             }
         }
    }

    /**
     * 获取current buffer
     * @param baseBuffer
     */
    public void loadCurrent(BaseBuffer baseBuffer, String nextId){
        if(baseBuffer.getBuffers()[baseBuffer.nextPos()] == null){
            baseBuffer.getBuffers()[baseBuffer.getCurrentPos()] = romoteLoadNextBuffer(baseBuffer.getKey());
        }else {
            baseBuffer.getBuffers()[baseBuffer.getCurrentPos()] = null;
            baseBuffer.switchPos();
            baseBuffer.setAlreadyLoadBuffer(false);
        }
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
            return initBaseBuffer(bizTag);
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

        if(baseBuffer == null){
            synchronized (baseMap){
                if(baseMap.get(bizTag) != null){
                    return baseMap.get(bizTag);
                }
                try{
                    BaseBuffer buffer = createBaseBuffer(bizTag);
                    baseMap.put(bizTag, buffer);
                    return buffer;
                }catch (Exception e){
                    throw new IdException("base buffer init error!msg={},message=" + e.getMessage());
                }
            }
        }
        return baseBuffer;
    }

    /**
     * 加载下一个缓存，超过设置阈值 获取下一个缓存
     *
     * @param baseBuffer
     * @param nextId
     */
    protected void loadNextBuffer(BaseBuffer baseBuffer, String nextId){

        //是否需要获取下一缓存，维护nextReady状态
        if(!baseBuffer.isAlreadyLoadBuffer()
                && baseBuffer.isloadNextBuffer(nextId)
                && baseBuffer.getThreadRunning().compareAndSet(false, true)){

            String bizTag = baseBuffer.getKey();
            executor.execute(()->{
                try {
                    synchronized (baseBuffer){
                        T buffer =  romoteLoadNextBuffer(bizTag);
                        baseBuffer.getBuffers()[baseBuffer.nextPos()] = buffer;
                        baseBuffer.setAlreadyLoadBuffer(true);
                    }
                    if(logger.isDebugEnabled()){
                        logger.debug("load next buffer end ... ,bizTag={}, baseBuffer={}.", bizTag, JSON.toJSONString(baseBuffer));
                    }
                }catch (Exception e){
                    logger.error("load next buffer error, buffer is null!!");

                }finally {
                    baseBuffer.getThreadRunning().set(false);
                }
            });
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
    protected abstract T romoteLoadNextBuffer(String bizTag);

}
