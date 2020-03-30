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

}
