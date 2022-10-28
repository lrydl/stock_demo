package com.smallrig.mall.template.service.impl;

import com.smallrig.mall.template.entity.Order;
import com.smallrig.mall.template.entity.Product;
import com.smallrig.mall.template.mapper.OrderMapper;
import com.smallrig.mall.template.mapper.ProductMapper;
import com.smallrig.mall.template.service.OrderService;
import com.smallrig.mall.template.service.ProductService;
import com.smallrig.mall.template.service.TranService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class TranServiceImpl implements TranService {

    @Resource
    private OrderService orderService;

    @Resource
    private ProductService productService;

    //@Transactional , 默认RuntimeException和Error

    //REQUIRED,SUPPORTS,MANDATORY,REQUIRES_NEW,NOT_SUPPORTED,NEVER,NESTED
    @Override
    @Transactional
    public void testTran()   {

        orderService.saveOrder();

        try{
            productService.saveProduct();
        }catch (Exception e){

        }
    }



}
