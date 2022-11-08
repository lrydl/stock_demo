package com.smallrig.mall.template;

import com.smallrig.extension.dto.BizScenario;
import com.smallrig.extension.ext.ExtensionExecutor;
import com.smallrig.mall.template.extension.ExtVersionExtPt;
import com.smallrig.mall.template.plugin.BeanListFactoryBean;
import com.smallrig.mall.template.plugin.Vehicle;
import com.smallrig.mall.template.service.TranService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.util.List;

@MapperScan("com.smallrig.mall.template.mapper")
@SpringBootApplication//(exclude = DataSourceAutoConfiguration.class)
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true)
@EnablePluginRegistries(Vehicle.class)
public class TemplateApplication implements CommandLineRunner {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TemplateApplication.class, args);
        TranService tranService = context.getBean(TranService.class);
        tranService.testTran();
    }

    @Autowired
    @Qualifier("vehicleRegistry")
    PluginRegistry<Vehicle, String> vehicleRegistry;


    @Autowired
    private BeanListFactoryBean beanListFactoryBean;

    @Resource
    private ExtensionExecutor extensionExecutor;


    @Override
    public void run(String... args) throws Exception {
        beanListFactoryBean.getObject();
        List<Vehicle> vehicles = vehicleRegistry.getPlugins();
        for (Vehicle vehicle : vehicles) {
            vehicle.reachGoal("1");
        }

        extensionExecutor.executeVoid(ExtVersionExtPt.class, BizScenario.valueOf("v1"),ExtVersionExtPt::sayHello);
    }
}
