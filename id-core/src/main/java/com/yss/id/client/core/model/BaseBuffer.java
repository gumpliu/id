package com.yss.id.client.core.model;

/**
 * 双buffer
 */
public class BaseBuffer<T> {
    private String key;
    protected T[] buffers;
    private volatile int currentPos; //当前的使用的segment的index
    private volatile int step;
    private volatile int minStep;
    private volatile boolean nextReady; //下一个segment是否处于可切换状态
    private volatile boolean alreadyLoadBuffer; //是否已经换成另一缓存


    public BaseBuffer() {
        currentPos = 0;
    }

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

    public int getMinStep() {
        return minStep;
    }

    public void setMinStep(int minStep) {
        this.minStep = minStep;
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

    public boolean isAlreadyLoadBuffer() {
        return alreadyLoadBuffer;
    }

    public void setAlreadyLoadBuffer(boolean alreadyLoadBuffer) {
        this.alreadyLoadBuffer = alreadyLoadBuffer;
    }
}
