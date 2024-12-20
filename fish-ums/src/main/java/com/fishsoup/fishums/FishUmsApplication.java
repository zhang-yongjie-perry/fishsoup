package com.fishsoup.fishums;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.fishsoup.fishums.mapper")
public class FishUmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FishUmsApplication.class, args);
    }

}
