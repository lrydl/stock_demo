package com.smallrig.mall.template.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallrig.mall.template.entity.Order;
import com.smallrig.mall.template.mapper.OrderMapper;
import com.smallrig.mall.template.mapper.ProductMapper;
import com.smallrig.mall.template.request.OrderReq;
import com.smallrig.mall.template.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl  extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private ProductMapper productMapper;

    @Override
    @Transactional
    public void saveOrder(List<Order> orders){
        int sum = orders.stream().mapToInt(e -> e.getBuyNum()).sum();
        //扣减库存，生成订单
        int affect = orderMapper.saveBatch(orders);

        int aff = productMapper.decrStock(orders.get(0).getProductId(), sum);
        //失败处理 todo
        if(aff<=0){
            log.error("扣减库存失败");
        }
        if(affect!=orders.size()){
            log.error("保存订单失败");
        }
    }

}
