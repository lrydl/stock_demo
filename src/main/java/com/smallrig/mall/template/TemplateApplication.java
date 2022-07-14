package com.smallrig.mall.template;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.smallrig.mall.template.mapper")
@SpringBootApplication//(exclude = DataSourceAutoConfiguration.class)
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true)
public class TemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class, args);
    }

}
