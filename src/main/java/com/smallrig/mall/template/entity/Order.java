package com.smallrig.mall.template.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * @author:刘仁有
 * @desc:
 * @email:953506233@qq.com
 * @data:2019/6/20
 */
@Data
public class Order extends Model implements Serializable  {
    private int id;
    private String name;
    private int productId;
    private int userId;
    private int buyNum;
}
