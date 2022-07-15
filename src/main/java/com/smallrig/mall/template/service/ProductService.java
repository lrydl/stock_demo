package com.smallrig.mall.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallrig.mall.template.entity.Order;
import com.smallrig.mall.template.entity.Product;
import com.smallrig.mall.template.mapper.OrderMapper;

public interface ProductService  extends IService<Product> {
    int queryStock(int skuId);
}
