package com.jzo2o.orders.manager;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 订单管理微服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.jzo2o.orders.manager")
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableCaching
@MapperScan("com.jzo2o.orders.manager.mapper.comment")
@Slf4j
public class OrdersManagerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OrdersManagerApplication.class)
                .build(args)
                .run(args);
        log.info("jzo2o-orders-manager started");
    }
}