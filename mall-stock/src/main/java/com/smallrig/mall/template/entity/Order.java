package com.smallrig.mall.template.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author:刘仁有
 * @desc:
 * @email:953506233@qq.com
 * @data:2019/6/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("sorder")
public class Order extends Model implements Serializable  {
    private int id;
    private long orderSn;//如果两条记录的orderSn一样,说明是属于一个订单的
    private int productId;
    private int userId;
    private int buyNum;
}
