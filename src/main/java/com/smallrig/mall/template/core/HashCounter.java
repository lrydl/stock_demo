package com.smallrig.mall.template.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class HashCounter implements Counter{


    private Map<Integer, CounterHelper> counterMap = new ConcurrentHashMap<>();
    protected int intervalInMs;//1000ms

    public HashCounter(int intervalInMs) {
        this.intervalInMs = intervalInMs;
    }

    @Override
    public boolean addCounter(int skuId,int count){
        CounterHelper counterHelper = counterMap.computeIfAbsent(skuId,(k)->new CounterHelper());

        long curTime = System.currentTimeMillis();
        long lastTime = counterHelper.getLastTime();
        LongAdder counter = counterHelper.getCounter();

        if(curTime-lastTime>intervalInMs){
            counter.reset();
        }
        counter.add(count);

        if(counter.sum()>=threshold){
            counterHelper.setLastTime(curTime);
            counter.reset();
            //达到阈值
            log.info("达到tps阈值,skuId="+skuId);
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        HashCounter hashCounter = new HashCounter(1000);
//        hashCounter.normalTest();
        hashCounter.bugTest();
    }

    private void normalTest() throws InterruptedException {
        for(int i=0;i<100000;i++){
            addCounter(1,1);
            Thread.sleep(9);
        }
    }

    /**
     * bug desc: 1____2____3
     * 假设某时刻CounterHelper里的lastTime=1,
     * 在1.5至2来了90个请求,
     * 2s时刻由于curTime-lastTime>1000,计数器被清空,
     * 接着2s至2.5又来了90个请求仍然不会触发阈值
     * 1.5至2.5一共来了180个请求, 明显不符合我们预期, 这是固定窗口的一个弊端
     */
    private void bugTest() throws InterruptedException {

        addCounter(1,1);//让lastTime=1
        Thread.sleep(500);//休眠500ms,让下面90个请求打到1.5至2这个区间
        for(int i=0;i<90;i++){//时刻1.5处
            addCounter(1,1);
        }
        Thread.sleep(500);//再休眠500ms,让时钟走到2s处
        //接着2到2.5再来90个请求
        for(int i=0;i<90;i++){
            addCounter(1,1);
        }
        //最终没有达到阈值,不符合预期, 使用滑动窗口解决 todo

        //固定窗口
        // |____|____|: 第一个窗口的后半部分和第二个窗口的前半部分加起来 可能超过阈值,但是却感知不了,这是因为固定窗口滑动的粒度太大了,可以使用滑动窗口缩小滑动的粒度
    }

    @Data
    class CounterHelper{
        private LongAdder counter;
        private long lastTime;

        public CounterHelper(){
            this.counter = new LongAdder();
            this.lastTime = System.currentTimeMillis();
        }
    }
}
