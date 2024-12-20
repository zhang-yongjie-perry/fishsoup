package com.fishsoup.fishchat;

import com.fishsoup.fishchat.netty.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FishChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(FishChatApplication.class, args);
    }

}
