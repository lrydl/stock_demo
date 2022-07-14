package com.smallrig.mall.template.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class SlideWindow implements Counter{

    protected int windowLengthInMs;//500ms，窗口的一个小格子的时长
    protected int sampleCount;//2，格子数
    protected int intervalInMs;//1000ms

    private final ReentrantLock updateLock = new ReentrantLock();
    private final AtomicReferenceArray<Bucket> array;

    /**
     * |___|___|___|___|___|
     * sampleCount = 5
     * intervalInMs = 1000
     * 其实就是统计1s,然后分为5个bucket
     * @param sampleCount 整个窗口分几个bucket,分的多一点越精确
     * @param intervalInMs 整个窗口时间
     */
    public SlideWindow(int sampleCount, int intervalInMs) {
        this.sampleCount = sampleCount;
        this.intervalInMs = intervalInMs;
        this.windowLengthInMs = intervalInMs/sampleCount;
        this.array = new AtomicReferenceArray(sampleCount);
    }


    @Override
    public boolean addCounter(int skuId, int count) {
        Bucket bucket = getBucket();
        bucket.addCount(skuId,count);
        return count(skuId)>=threshold;
    }

    public Bucket getBucket(){
        long curTime = System.currentTimeMillis();
        int idx = (int) ((curTime/windowLengthInMs)%sampleCount);
        long windowStart = (curTime - curTime%windowLengthInMs);
        Bucket old = array.get(idx);

        while(true){
            if(null==old){
                Bucket bucket = new Bucket(windowLengthInMs,windowStart,new LongAdder());
                if(array.compareAndSet(idx,null,bucket)){
                    return bucket;
                }else{
                    Thread.yield();
                }
            }else{
                if(old.getWindowStart()==windowStart){
                    return old;
                }else if(old.getWindowStart()<windowStart){
                    try{
                        updateLock.lock();
                        //重置旧bucket
                        old.reset(windowStart);
                    }finally {
                        updateLock.unlock();
                    }
                }else{
                    //can not reach
                    System.out.println("can not reach");
                }
            }
        }

    }



    public int count(int skuId){
        int sum = 0;
        for(int i=0;i<array.length();i++){
            if(null!=array.get(i)){
                sum += array.get(i).getCounter(skuId).sum();
            }
        }
        return sum;
    }


    public static void main(String[] args) throws IOException {
        SlideWindow slideWindow = new SlideWindow(10,1000);

        Random random = new Random();
        for(int i=0;i<10;i++){
            new Thread(()->{
                while(true){
                    Bucket bucket = slideWindow.getBucket();
                    bucket.addCount(1,1);
                    try {
                        Thread.sleep(random.nextInt(500));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        new Thread(()->{
            while(true){
                System.out.println(slideWindow.count(1));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.in.read();
    }


}
