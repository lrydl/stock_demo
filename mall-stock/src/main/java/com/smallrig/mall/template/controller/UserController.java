package com.smallrig.mall.template.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.smallrig.mall.template.core.Counter;
import com.smallrig.mall.template.core.MergeQueue;
import com.smallrig.mall.template.core.SlideWindow;
import com.smallrig.mall.template.entity.StockLog;
import com.smallrig.mall.template.request.OrderReq;
import com.smallrig.mall.template.service.StockLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;


@RestController
@RequestMapping("user")
@Slf4j
public class UserController{

    Random random = new Random();

    private Counter slideWindow = new SlideWindow(10,1000);

    @Resource
    private MergeQueue mergeQueue;

    @Resource
    private StockLogService stockLogServiceImpl;

    volatile int maxProductId = 5; //爆品sku只能几个,3-5个的时候tps能到4000多,10个只能到2000,cpu都吃完了

    @PostConstruct
    public void init(){
        new Thread(()->{
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //模拟20s后让9，10不再过来了，测试撤销线程
            maxProductId = 3;
        }).start();
    }



    @GetMapping("/submitOrder")
    public void submitOrder() throws InterruptedException {

        int userId = random.nextInt(4)+1;//1-4
        List<OrderReq.SkuReq> skuReqs = new ArrayList<>();
        int skuNum = random.nextInt(5)+1;
        Set<Integer> skuIds = new HashSet<>();
        for(int i=0;i<skuNum;i++){
            int skuId = random.nextInt(maxProductId)+1;//1-2
            if(!skuIds.contains(skuId)){
                int buyNum = random.nextInt(5)+1;//1-5
                skuReqs.add(new OrderReq.SkuReq(skuId,buyNum));
            }
            skuIds.add(skuId);
        }
        OrderReq orderReq = OrderReq.builder().userId(userId).skuReqs(skuReqs).businessId(IdWorker.getId()).build();

        //只要有一个sku打到tps阈值,就进行合并队列操作
        boolean generateQueue = false;
        for (OrderReq.SkuReq skuReq : orderReq.getSkuReqs()) {
            boolean f = slideWindow.addCounter(skuReq.getSkuId(), 1);
            if(f){
                generateQueue = true;
            }
        }

        //tps达到阈值,开始合并请求处理
        if(generateQueue){
            MergeQueue.Result result = mergeQueue.offer(orderReq);
            if(!result.isSuccess()){
                log.info("秒杀失败,result="+result);
            }
        }else{
            //否则单个处理
            List<StockLog> stockLogs = new ArrayList<>();
            for (OrderReq.SkuReq skuReq : skuReqs) {
                stockLogs.add(StockLog.builder().businessId(orderReq.getBusinessId()).
                        userId(orderReq.getUserId()).buyNum(skuReq.getBuyNum()).skuId(skuReq.getSkuId()).build());
            }
            try{
                stockLogServiceImpl.saveStockLog(stockLogs);
            }catch (Exception e){

            }
        }
    }

}
