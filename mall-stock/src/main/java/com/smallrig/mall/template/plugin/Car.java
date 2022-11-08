package com.smallrig.mall.template.plugin;

import org.springframework.stereotype.Service;

@Service
public class Car implements Vehicle {


    @Override
    public void reachGoal(String wether) {
        supports(wether);
        System.out.println("汽车。。。");
    }

    @Override
    public boolean supports(String delimiter) {
        return false;
    }
}
