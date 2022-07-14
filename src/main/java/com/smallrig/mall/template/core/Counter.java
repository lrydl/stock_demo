package com.smallrig.mall.template.core;

public interface Counter {

    long threshold = 10;

    boolean addCounter(int skuId,int count);

}
