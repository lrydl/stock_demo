package com.smallrig.mall.template.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class Bucket {

    private int windowLengthInMs;//500ms，窗口的一个小格子的时长
    private long windowStart;
    //skuId,计数器
    private Map<Integer,LongAdder> counterMap;

    public Bucket(int windowLengthInMs, long windowStart, LongAdder counter) {
        this.windowLengthInMs = windowLengthInMs;
        this.windowStart = windowStart;
        this.counterMap = new ConcurrentHashMap<>();
    }

    public void reset(long windowStart){
        for (LongAdder counter : counterMap.values()) {
            counter.reset();
        }
        this.windowStart = windowStart;
    }

    public int getWindowLengthInMs() {
        return windowLengthInMs;
    }

    public void setWindowLengthInMs(int windowLengthInMs) {
        this.windowLengthInMs = windowLengthInMs;
    }

    public long getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(long windowStart) {
        this.windowStart = windowStart;
    }

    public LongAdder getCounter(int skuId) {
        return counterMap.computeIfAbsent(skuId,(k)->new LongAdder());
    }

    public void addCount(int skuId,int count){
        getCounter(skuId).add(count);
    }
}
