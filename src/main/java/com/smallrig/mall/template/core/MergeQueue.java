package com.smallrig.mall.template.core;

import com.smallrig.mall.template.entity.Order;
import com.smallrig.mall.template.mapper.OrderMapper;
import com.smallrig.mall.template.mapper.ProductMapper;
import com.smallrig.mall.template.request.OrderReq;
import com.smallrig.mall.template.service.OrderService;
import com.smallrig.mall.template.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MergeQueue {

   @Resource
   private OrderService orderServiceImpl;

   @Resource
   private ProductService productServiceImpl;

   private static int threshold = 3;//空轮询若干次撤销异步线程，当tps下降如何处理？ 当tps下降时，在controller层走的是单个扣减逻辑，即请求不会打到MergeQueue,所以只需要检测空轮询即可


    //Q2: 扣减库存时是否需要对skuid分组呢, 如果需要那tps肯定急速下降, 如果采用不同sku进入不同队列的方式,那么如何消费这些队列呢
    //A2: 对每一个sku分配一个队列,为每个队列开异步线程, 只能针对爆品,顶多十来个

    //Q3: 队列的创建(达到设定的并发阈值)与销毁(达到设定的空轮询阈值)
    //A3: 设计简单的计数器即可?(滑动窗口更好), 空轮询若干次后移除掉队列

    //Q4: 订单是如何生成的,生成时机?
    //A4: 上游生成订单,然后扣减库存,扣失败了回滚订单

    //Q5: 一个订单多个sku如何处理?
    //A5: 根据skuId拆单, 使用CountDownLatch(sku数量)阻塞上游调用方

    //Q6:某个时刻宕机, 队列还没消费完如何处理? todo

    private static Map<Integer,AsyncThread> threadMap = new ConcurrentHashMap<>();


    public Result offer(OrderReq request) throws InterruptedException {

        CountDownLatch cdl = new CountDownLatch(request.getSkuReqs().size());
        List<RequestPromise> requestPromises = new ArrayList<>();

        for (OrderReq.SkuReq skuReq : request.getSkuReqs()) {

            AsyncThread asyncThread = threadMap.computeIfAbsent(skuReq.getSkuId(),
                    (k)->new AsyncThread(skuReq.getSkuId(),orderServiceImpl,productServiceImpl));

            //根据sku拆单
            Order order = Order.builder().buyNum(skuReq.getBuyNum()).userId(request.getUserId()).productId(skuReq.getSkuId()).orderSn(request.getOrderSn()).build();
            RequestPromise requestPromise = new RequestPromise(order,cdl);
            requestPromises.add(requestPromise);
            boolean enqueueSuccess = asyncThread.offer(requestPromise, 100, TimeUnit.MILLISECONDS);
            //这里可能会出现一种情况：当出现三次空轮训后，撤销线程，突然来了一波高峰，可能会直接返回false，不知道可不可行，这种处理方式？ todo
            if (!enqueueSuccess) {
                return new Result(false, "系统繁忙");
            }
        }

        cdl.await(300,TimeUnit.MILLISECONDS);

        for (RequestPromise requestPromise : requestPromises) {
            if (requestPromise.getResult() == null) {
                //可能需要回滚扣掉的库存 todo
                //库存流水表 todo
                //Q1:上游业务方等待超时,返回给用户秒杀失败, 下游可能是处于阻塞态, 之后可能扣减库存成功了, 那这里上游就需要做下补偿,把库存加回来 todo
                return new Result(false, "等待超时");
            }
            if(!requestPromise.getResult().isSuccess()){
                return requestPromise.getResult();
            }
        }

        return Result.ok();
    }


    @Slf4j
    public static class AsyncThread extends Thread{
        private int skuId;
        private BlockingQueue<MergeQueue.RequestPromise> queue;
        private int emptyCounter = 0;
        private boolean running = true;
        private OrderService orderServiceImpl;
        private ProductService productServiceImpl;

        public AsyncThread(int skuId, OrderService orderServiceImpl,ProductService productServiceImpl) {
            super("mergeThread,skuId="+skuId);
            this.orderServiceImpl = orderServiceImpl;
            this.productServiceImpl = productServiceImpl;
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
                    //这里撤销可能有点问题, 是否需要出现连续几次空轮询后才撤销? 撤销的阈值该如何确定? todo
                    if(emptyCounter>=threshold){
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

                emptyCounter = 0;// 重置0,连续空轮询才撤销

                long start = System.currentTimeMillis();

                List<MergeQueue.RequestPromise> list = new ArrayList<>();
                int batchSize = queue.size();
                int buySum = 0;
                for (int i = 0; i < batchSize; i++) {
                    MergeQueue.RequestPromise requestPromise = queue.poll();
                    buySum += requestPromise.getRequest().getBuyNum();
                    list.add(requestPromise);
                }

                log.info(Thread.currentThread().getName() + ":合并扣减库存:" + list);

                List<Order> orders = list.stream().map(v -> v.getRequest()).collect(Collectors.toList());
                boolean decrStockOk = orderServiceImpl.saveOrder(orders);

                //批量扣减成功了,直接批量返回
                if(decrStockOk){
                    // notify user
                    list.forEach(requestPromise -> {
                        requestPromise.setResult(new MergeQueue.Result(true, "ok"));
                        requestPromise.signal();
                    });
                }else{
                    //退化成循环扣减
                    degrade(list);
                }

                list.clear();

                long costTime = System.currentTimeMillis()-start;
                if(costTime>5){
                    log.info("扣减库存花费时间="+costTime);
                }
            }
        }


        //走到降级tps下降的会比较狠,因为多了一个查库存, 扣减库存也变成单批扣减了
        private void degrade(List<MergeQueue.RequestPromise> list){
            int skuId = list.get(0).getRequest().getProductId();
            int stock = productServiceImpl.queryStock(skuId);
            //查询库存数量
            if(stock<=0){
                // notify user
                list.forEach(requestPromise -> {
                    requestPromise.setResult(new MergeQueue.Result(false, "库存不足"));
                    requestPromise.signal();
                });
            }else{
                //根据购买数量倒序排序,尽量让买的多的客户买到
                list.sort((o1,o2)->o2.getRequest().getBuyNum()-o1.getRequest().getBuyNum());
                //库存不足退化成循环
                for (MergeQueue.RequestPromise requestPromise : list) {
                    if(stock>=requestPromise.getRequest().getBuyNum()){
                        boolean f = orderServiceImpl.saveOrder(Arrays.asList(requestPromise.getRequest()));
                        if(f){
                            //通知用户成功
                            requestPromise.setResult(new MergeQueue.Result(true, "ok"));
                            requestPromise.signal();
                            stock -= requestPromise.getRequest().getBuyNum();
                        }else{
                            //通知用户失败
                            requestPromise.setResult(new MergeQueue.Result(false, "库存不足"));
                            requestPromise.signal();
                        }
                    }else{
                        //通知用户失败
                        requestPromise.setResult(new MergeQueue.Result(false, "库存不足"));
                        requestPromise.signal();
                    }
                }
            }
        }
    }


    public static class RequestPromise {
        private Order request;
        private Result result;
        private CountDownLatch cdl;
        public RequestPromise(Order request,CountDownLatch cdl) {
            this.request = request;
            this.cdl = cdl;
        }

        public void await(long mills) throws InterruptedException {
            LockSupport.parkNanos(mills*1000000);
        }

        public void signal(){
            cdl.countDown();
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

        public static Result ok(){
            return new Result(true,"ok");
        }
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
