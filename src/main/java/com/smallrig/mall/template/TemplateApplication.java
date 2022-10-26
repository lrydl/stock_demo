package com.smallrig.mall.template;

import com.smallrig.mall.template.plugin.BeanListFactoryBean;
import com.smallrig.mall.template.plugin.Vehicle;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@MapperScan("com.smallrig.mall.template.mapper")
@SpringBootApplication//(exclude = DataSourceAutoConfiguration.class)
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true)
@EnablePluginRegistries(Vehicle.class)
public class TemplateApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class, args);
    }

    @Autowired
    @Qualifier("vehicleRegistry")
    PluginRegistry<Vehicle, String> vehicleRegistry;


    @Autowired
    private BeanListFactoryBean beanListFactoryBean;

    @Override
    public void run(String... args) throws Exception {
        beanListFactoryBean.getObject();
        List<Vehicle> vehicles = vehicleRegistry.getPlugins();
        for (Vehicle vehicle : vehicles) {
            vehicle.reachGoal("1");
        }
    }
}
