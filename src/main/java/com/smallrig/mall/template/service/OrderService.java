package com.smallrig.mall.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallrig.mall.template.entity.Order;
import com.smallrig.mall.template.request.OrderReq;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService extends IService<Order> {
    @Transactional
    void saveOrder(List<Order> orders);
}
