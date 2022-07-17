package com.smallrig.mall.template.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockLog extends Model {

    private int id;
    private int skuId;
    private int buyNum;//一共扣减了多少件
    private int rollbackNum;//一共回滚了多少件
    private long businessId;//库存扣减流水号
    private int userId;

}
