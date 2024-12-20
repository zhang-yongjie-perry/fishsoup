package com.fishsoup.fishweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.fishsoup.fishweb.mapper")
public class FishWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(FishWebApplication.class, args);
    }

}
