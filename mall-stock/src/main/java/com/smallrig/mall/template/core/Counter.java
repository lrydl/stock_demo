package com.smallrig.mall.template.core;

public interface Counter {

    long threshold = 100;

    boolean addCounter(int skuId,int count);

}
