package com.fishsoup.fishgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FishGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FishGatewayApplication.class, args);
    }

}
