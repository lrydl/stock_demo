package com.smallrig.mall.template.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallrig.mall.template.entity.StockLog;
import com.smallrig.mall.template.mapper.ProductMapper;
import com.smallrig.mall.template.mapper.StockLogMapper;
import com.smallrig.mall.template.service.StockLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class StockLogServiceImpl extends ServiceImpl<StockLogMapper, StockLog> implements StockLogService {

    @Resource
    private ProductMapper productMapper;

    @Override
    @Transactional
    public boolean saveStockLog(List<StockLog> stockLogList){
        int sum = stockLogList.stream().mapToInt(e -> e.getBuyNum()).sum();
        //扣减库存，
        saveBatch(stockLogList);

        int aff = productMapper.decrStock(stockLogList.get(0).getSkuId(), sum);
        if(aff<=0){
            log.error("扣减库存失败");
            //我们希望扣减库存成功和记录流水表是事务的
            throw new RuntimeException("扣减库存失败");
        }
        return true;
    }

}
