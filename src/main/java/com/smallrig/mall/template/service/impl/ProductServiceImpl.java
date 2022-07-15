package com.smallrig.mall.template.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallrig.mall.template.entity.Order;
import com.smallrig.mall.template.entity.Product;
import com.smallrig.mall.template.mapper.OrderMapper;
import com.smallrig.mall.template.mapper.ProductMapper;
import com.smallrig.mall.template.service.OrderService;
import com.smallrig.mall.template.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public int queryStock(int skuId){
        Product product = lambdaQuery().eq(Product::getId,skuId).select(Product::getStock).one();
        if(null==product){
            return 0;
        }
        return product.getStock();
    }
}
