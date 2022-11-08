package com.smallrig.mall.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smallrig.mall.template.entity.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService extends IService<Order> {

    void saveOrder();

    @Transactional
    boolean saveOrder(List<Order> orders);
}
