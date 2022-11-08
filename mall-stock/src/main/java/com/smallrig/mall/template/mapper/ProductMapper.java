package com.smallrig.mall.template.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smallrig.mall.template.entity.Product;
import com.smallrig.mall.template.entity.dto.DecrStock;

import java.util.List;

/**
 * @author:刘仁有
 * @desc:
 * @email:953506233@qq.com
 * @data:2019/6/20
 */
public interface ProductMapper extends BaseMapper<Product> {

    //扣减库存
    int decrStock(int productId,int buyNum);

    int decrStockBatch(List<DecrStock> sku);

}
