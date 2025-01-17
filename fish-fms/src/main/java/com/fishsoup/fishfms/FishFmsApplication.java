package com.fishsoup.fishfms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FishFmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FishFmsApplication.class, args);
    }

}
