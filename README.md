### 订单服务

#### 服务说明：


##### 目录结构说明：
##### src/main/java
    com.smallrig.mall.xxx
        aspect———————————————里面是切面web请求日志
        common———————————————公共对象
             bean———————————————公共bean
             cache——————————————缓存对象
             constant———————————公共常量
        config———————————————里面是配置
        mapper——————————————————里面是数据库操作（MybatisPlus）
        entity———————————————里面是数据库实体
        exception————————————里面是自定义异常
        filter———————————————里面是过滤器
        handler——————————————里面是全局异常处理
        interceptor——————————里面是拦截器
        listener—————————————里面是监听器
        service——————————————里面是接口及实现类
        util—————————————————里面是工具类
        controller——————————————————里面是controller
    StartApplication.java————启动类
##### src/main/resources
        static————————————————静态资源文件夹
        application-dev.yml———开发环境配置
        application-prod.yml——生产环境配置
        application-test.yml——测试环境配置
        application.yml———————多环境配置
        logback-spring.xml————logback日志

##### src/main/test
    com.hkb.springboot
        service
            BaseTest.java—————————————————测试基类
            StudentServiceTest.java———————接口测试
        web
            StudentControllerTest.java————controller测试