package com.yss.id.core.model;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 双buffer
 */
public abstract class BaseBuffer<T> {
    private String key;
    protected T[] buffers;
    private volatile int currentPos; //当前的使用的segment的index
    private volatile int step;
    private volatile boolean nextReady; //下一个segment是否处于可切换状态
    private volatile boolean alreadyLoadBuffer; //是否已经换成另一缓存
    private final AtomicBoolean threadRunning;
    private final ReadWriteLock lock;



    public BaseBuffer() {
        threadRunning = new AtomicBoolean(false);
        currentPos = 0;
        lock = new ReentrantReadWriteLock();

    }


    /**
     * 根据当前缓存获取id
     * @return
     */
    public abstract String nextId();

    /**
     * 是否切换至下一缓存，当id消耗完后切换
     *
     * @return
     */
    public  abstract boolean switchBufer(String nextId);

    /**
     * 当前buffer 是否为空
     * @return
     */
    public abstract boolean isCurrentEmpty();

    /**
     * 当前buffer 最大值
     * @return
     */
    public abstract BigDecimal getMaxId();

    public boolean isInitBuffer(String nextId){
        return false;
    }

    /**
     * 是否需要获取二级缓存
     *
     * @param nextId
     * @return
     */
    public abstract boolean isloadNextBuffer(String nextId);


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public int nextPos() {
        return (currentPos + 1) % 2;
    }

    public void switchPos() {
        currentPos = nextPos();
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public T[] getBuffers() {
        return buffers;
    }

    public void setBuffers(T[] buffers) {
        this.buffers = buffers;
    }

    public T getCurrent() {
        return buffers[getCurrentPos()];
    }

    public boolean isNextReady() {
        return nextReady;
    }

    public void setNextReady(boolean nextReady) {
        this.nextReady = nextReady;
    }

    public AtomicBoolean getThreadRunning() {
        return threadRunning;
    }

    public boolean isAlreadyLoadBuffer() {
        return alreadyLoadBuffer;
    }

    public void setAlreadyLoadBuffer(boolean alreadyLoadBuffer) {
        this.alreadyLoadBuffer = alreadyLoadBuffer;
    }

    public ReadWriteLock getLock() {
        return lock;
    }
}
