package com.smallrig.mall.template.controller;

import com.smallrig.mall.template.core.MergeQueue;
import com.smallrig.mall.template.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Random;


@RestController
@RequestMapping("user")
@Slf4j
public class UserController{

    Random random = new Random();

    @Resource
    private MergeQueue mergeQueue;

    @GetMapping("/submitOrder")
    public void submitOrder() throws InterruptedException {
        Order order = new Order();
        int userId = random.nextInt(4)+1;//1-4
        int productId= random.nextInt(1)+1;//1-2
        int buyNum = random.nextInt(5)+1;//1-5
        order.setUserId(userId);
        order.setProductId(productId);
        order.setBuyNum(buyNum);

        MergeQueue.Result result = mergeQueue.offer(order);

        if(!result.isSuccess()){
            log.info("秒杀失败,result="+result);
        }
    }

}
