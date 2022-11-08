package com.smallrig.mall.template.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Law
 * @version 1.0
 * @date 2020/4/2 11:45
 */
@Configuration
@EnableAsync
@Getter
@Setter
public class ThreadPoolConfig {


    @Bean
    public Executor taskExecutor() {

        // transmittable代理，使得在新建线程或者线程池执行任务的时候，能够共享ThreadLocal变量
        return TtlExecutors.getTtlExecutor(threadPoolTaskExecutor());
    }


    @Primary
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(20);
        // 设置最大线程数
        executor.setMaxPoolSize(20);
        // 设置队列容量
//        executor.setQueueCapacity(2);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(300);
        // 设置默认线程名称
        executor.setThreadNamePrefix("taskExecutor-");
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

}