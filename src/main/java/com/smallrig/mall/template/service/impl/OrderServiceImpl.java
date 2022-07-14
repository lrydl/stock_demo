package com.smallrig.mall.template.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallrig.mall.template.entity.Order;
import com.smallrig.mall.template.mapper.OrderMapper;
import com.smallrig.mall.template.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl  extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
