package com.smallrig.mall.template.controller;

import com.smallrig.mall.template.core.Counter;
import com.smallrig.mall.template.core.MergeQueue;
import com.smallrig.mall.template.core.SlideWindow;
import com.smallrig.mall.template.entity.Order;
import com.smallrig.mall.template.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/submitOrder")
    public void submitOrder() throws InterruptedException {
        Order order = new Order();
        int userId = random.nextInt(4)+1;//1-4
        int productId= random.nextInt(1)+1;//1-2
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
