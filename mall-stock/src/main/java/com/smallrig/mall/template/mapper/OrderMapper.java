package com.smallrig.mall.template.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smallrig.mall.template.entity.Order;

import java.util.List;

/**
 * @author:刘仁有
 * @desc:
 * @email:953506233@qq.com
 * @data:2019/6/20
 */
public interface OrderMapper extends BaseMapper<Order> {
    int save(Order order);

    int saveBatch(List<Order> orders);

    int updateById(Order order);

    Order selectById(Integer id);
}
