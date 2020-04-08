package com.yss.id.client.core.generator;

import com.yss.id.client.core.model.BaseBuffer;
import com.yss.id.client.core.service.IdService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: id 生成器基础实现，统一实现双缓存
 * @Author gumpLiu
 * @Date 2020-03-26
 * @Version V1.0
 **/
public abstract class AbstractIdGenerator<T> {

    protected Map<String, BaseBuffer<T>> baseMap = new ConcurrentHashMap<String, BaseBuffer<T>>();

    protected IdService idService;

    public AbstractIdGenerator(IdService idService){
        this.idService = idService;
    }

    /**
     * 获取 nextId
     * @param bizTag
     * @return
     */
    public String nextId(String bizTag){

        BaseBuffer<T> baseBuffer = getBuffer(bizTag);

        String nextId = "";

        synchronized (baseBuffer){
            nextId = baseBuffer.nextId(baseBuffer.getCurrent());
            //获取下一缓存
            loadNextBuffer(bizTag);
            //nextId 等于maxId时，切换缓存
            if(baseBuffer.switchBufer(baseBuffer.getCurrent())){
                baseBuffer.switchPos();
                baseBuffer.setAlreadyLoadBuffer(false);
            }
        }

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
            baseBuffer = initBaseBuffer(bizTag);
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
            //todo 锁优化，可以移动至createBaseBffer中
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
     *
     * @param bizTag
     */
    protected void loadNextBuffer(String bizTag){

        BaseBuffer baseBuffer  = baseMap.get(bizTag);

        T currentBuffer = (T) baseBuffer.getCurrent();

        T nextBuffer = (T) baseBuffer.getBuffers()[baseBuffer.nextPos()];

        //是否需要获取下一缓存，维护nextReady状态

        if(!baseBuffer.isAlreadyLoadBuffer() && baseBuffer.isloadNextBuffer(currentBuffer, nextBuffer)){

            synchronized (baseBuffer){
                if(!baseBuffer.isAlreadyLoadBuffer()){
                    //远程调用服务获取id集合，并添加至缓存中
                    romoteLoadNextBuffer(bizTag);

                    baseBuffer.setAlreadyLoadBuffer(true);
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
