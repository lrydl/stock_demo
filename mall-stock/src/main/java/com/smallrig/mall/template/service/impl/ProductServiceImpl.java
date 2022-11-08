package com.smallrig.mall.template.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallrig.mall.template.entity.Product;
import com.smallrig.mall.template.mapper.ProductMapper;
import com.smallrig.mall.template.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Override
    public int queryStock(int skuId){
        Product product = lambdaQuery().eq(Product::getId,skuId).select(Product::getStock).one();
        if(null==product){
            return 0;
        }
        return product.getStock();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveProduct(){
        Product product = new Product();
        product.setPrice(1);
        product.setName("2121");
        productMapper.insert(product);
        int i = 1/0;
    }
}
