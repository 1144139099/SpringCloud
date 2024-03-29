package com.hlh.order;

import com.hlh.order.openfeign.GoodsDemoService;
import com.hlh.order.openfeign.ShopCartDemoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients={ShopCartDemoService.class, GoodsDemoService.class})
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
