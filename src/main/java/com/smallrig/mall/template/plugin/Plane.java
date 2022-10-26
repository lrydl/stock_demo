package com.smallrig.mall.template.plugin;

import org.springframework.stereotype.Service;

@Service
public class Plane implements Vehicle {


    @Override
    public void reachGoal(String wether) {

        System.out.println("飞机。。。");
    }

    @Override
    public boolean supports(String delimiter) {
        return false;
    }
}
