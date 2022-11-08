package com.smallrig.mall.template.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("t_product")
public class Product extends Model implements Serializable {

    private int id;
    private int price;
    private String name;
    private int stock;
    private String category;

}
