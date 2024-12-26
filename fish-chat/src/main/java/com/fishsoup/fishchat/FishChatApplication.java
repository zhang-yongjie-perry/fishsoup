package com.fishsoup.fishchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FishChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(FishChatApplication.class, args);
    }

}
