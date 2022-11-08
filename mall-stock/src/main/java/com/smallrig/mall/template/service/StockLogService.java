package com.smallrig.mall.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smallrig.mall.template.entity.StockLog;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StockLogService extends IService<StockLog> {

    @Transactional
    boolean saveStockLog(List<StockLog> stockLogList);
}
