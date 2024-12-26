package com.fishsoup.fishchat.controller;

import com.fishsoup.fishchat.netty.NettyServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final NettyServer nettyServer;

    @PostConstruct
    public void init() {
        new Thread(nettyServer).start();
    }
}
