package com.smallrig.mall.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smallrig.mall.template.entity.Product;

public interface ProductService  extends IService<Product> {
    int queryStock(int skuId);

    void saveProduct();
}
