package com.smallrig.mall.template.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderReq {

    private long businessId;//库存扣减流水号

    private int userId;

    private List<SkuReq> skuReqs;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkuReq{
        private int skuId;
        private int buyNum;

    }

}
