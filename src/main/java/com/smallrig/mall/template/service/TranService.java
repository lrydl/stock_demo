package com.smallrig.mall.template.service;

import org.springframework.transaction.annotation.Transactional;

public interface TranService {
    //REQUIRED,SUPPORTS,MANDATORY,REQUIRES_NEW,NOT_SUPPORTED,NEVER,NESTED
    @Transactional
    void testTran();
}
