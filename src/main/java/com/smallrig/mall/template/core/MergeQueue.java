package com.smallrig.mall.template.core;

import com.smallrig.mall.template.entity.Order;
import com.smallrig.mall.template.mapper.OrderMapper;
import com.smallrig.mall.template.mapper.ProductMapper;
import com.smallrig.mall.template.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MergeQueue {

   @Resource
   private OrderService orderServiceImpl;

   private int threshold = 3;//空轮询若干次撤销异步线程，当tps下降如何处理？ 当tps下降时，在controller层走的是单个扣减逻辑，所以检测只需要空轮询即可

    //Q1:上游业务方等待超时,返回给用户秒杀失败, 下游可能是处于阻塞态, 之后可能扣减库存成功了, 那这里上游就需要做下补偿,把库存加回来 todo

    //Q2: 扣减库存时是否需要对skuid分组呢, 如果需要那tps肯定急速下降, 如果采用不同sku进入不同队列的方式,那么如何消费这些队列呢 todo
    //A2: 对每一个sku分配一个队列,为每个队列开异步线程, 只能针对爆品,顶多十来个

    //Q3: 队列的创建(达到设定的并发阈值)与销毁(达到设定的空轮询阈值) todo
    //A3: 设计简单的计数器即可?(滑动窗口更好), 空轮询若干次后移除掉队列

    //Q4:订单是如何生成的,生成时机 ?
    //Q5:一个订单多个sku如何处理 todo
    //Q6:某个时刻宕机, 队列还没消费完如何处理?

    private Map<Integer,AsyncThread> threadMap = new ConcurrentHashMap<>();


    public Result offer(Order request) throws InterruptedException {

        AsyncThread asyncThread = threadMap.computeIfAbsent(request.getProductId(),
                (k)->new AsyncThread(request.getProductId()));

        RequestPromise requestPromise = new RequestPromise(request,Thread.currentThread());

        boolean enqueueSuccess = asyncThread.offer(requestPromise, 100, TimeUnit.MILLISECONDS);
        //这里可能会出现一种情况：当出现三次空轮训后，撤销线程，突然来了一波高峰，可能会直接返回false，不知道可不可行，这种处理方式？ todo
        if (!enqueueSuccess) {
            return new Result(false, "系统繁忙");
        }
        requestPromise.await(300);

        if (requestPromise.getResult() == null) {
            //可能需要回滚扣掉的库存 todo
            return new Result(false, "等待超时");
        }
        return requestPromise.getResult();
    }


    @Slf4j
    public class AsyncThread extends Thread{
        private int skuId;
        private BlockingQueue<MergeQueue.RequestPromise> queue;
        private int emptyCounter = 0;
        private boolean running = true;
        public AsyncThread(int skuId) {
            super("mergeThread,skuId="+skuId);
            this.skuId = skuId;
            this.queue = new LinkedBlockingQueue<>(10000);
            start();
        }


        public boolean isRunning() {
            return running;
        }

        public boolean offer(MergeQueue.RequestPromise requestPromise, long time, TimeUnit unit) throws InterruptedException {
            if(!isRunning()){
                return false;
            }
            return queue.offer(requestPromise,time,unit);
        }

        @Override
        public void run() {
            while (running) {
                if (queue.isEmpty()) {
                    emptyCounter++;
                    if(emptyCounter>threshold){
                        threadMap.remove(skuId);
                        running = false;
                        log.info("stop thread,threadName="+getName());
                    }
                    try {
                        Thread.sleep(100);
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                long start = System.currentTimeMillis();

                List<MergeQueue.RequestPromise> list = new ArrayList<>();
                int batchSize = queue.size();
                for (int i = 0; i < batchSize; i++) {
                    list.add(queue.poll());
                }

                log.info(Thread.currentThread().getName() + ":合并扣减库存:" + list);
                List<Order> orders = list.stream().map(v -> v.getRequest()).collect(Collectors.toList());
                orderServiceImpl.saveOrder(orders);

                //库存不足退化成循环 todo

                // notify user
                list.forEach(requestPromise -> {
                    requestPromise.setResult(new MergeQueue.Result(true, "ok"));
                    requestPromise.signal();
                });
                list.clear();

                long costTime = System.currentTimeMillis()-start;
                if(costTime>10){
                    log.info("扣减库存花费时间="+costTime);
                }
            }
        }
    }


    public static class RequestPromise {
        private Order request;
        private Result result;
        private Thread thread;

        public RequestPromise(Order request,Thread thread) {
            this.request = request;
            this.thread = thread;
        }


        public void await(long mills){
            LockSupport.parkNanos(mills*1000000);
        }

        public void signal(){
            LockSupport.unpark(thread);
        }

        public Order getRequest() {
            return request;
        }

        public void setRequest(Order request) {
            this.request = request;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "RequestPromise{" +
                    "userRequest=" + request +
                    ", result=" + result +
                    '}';
        }
    }

    public static class Result {
        private Boolean success;
        private String msg;


        public Result(boolean success, String msg) {
            this.success = success;
            this.msg = msg;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "success=" + success +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }
}
