package com.smallrig.mall.template.controller;

import com.smallrig.mall.template.core.Counter;
import com.smallrig.mall.template.core.MergeQueue;
import com.smallrig.mall.template.core.SlideWindow;
import com.smallrig.mall.template.entity.Order;
import com.smallrig.mall.template.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Random;


@RestController
@RequestMapping("user")
@Slf4j
public class UserController{

    Random random = new Random();

    private Counter slideWindow = new SlideWindow(10,1000);

    @Resource
    private MergeQueue mergeQueue;

    @Resource
    private OrderService orderServiceImpl;

    volatile int maxProductId = 10; //爆品sku只能几个,3-5个的时候tps能到4000多,10个只能到2000,cpu都吃完了

    @PostConstruct
    public void init(){
        new Thread(()->{
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //模拟20s后让9，10不再过来了，测试撤销线程
            maxProductId = 8;
        }).start();
    }

    @GetMapping("/submitOrder")
    public void submitOrder() throws InterruptedException {
        Order order = new Order();
        int userId = random.nextInt(4)+1;//1-4
        int productId= random.nextInt(maxProductId)+1;//1-2
        int buyNum = random.nextInt(5)+1;//1-5
        order.setUserId(userId);
        order.setProductId(productId);
        order.setBuyNum(buyNum);
        boolean generateQueue = slideWindow.addCounter(order.getProductId(), order.getBuyNum());

        //tps达到阈值,开始合并请求处理
        if(generateQueue){
            MergeQueue.Result result = mergeQueue.offer(order);
            if(!result.isSuccess()){
                log.info("秒杀失败,result="+result);
            }
        }else{
            //否则单个处理
            orderServiceImpl.saveOrder(Arrays.asList(order));
        }
    }

}
