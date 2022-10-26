package com.smallrig.mall.template.plugin;

import org.springframework.plugin.core.Plugin;

public interface Vehicle extends Plugin<String> {

    void reachGoal(String str);

}