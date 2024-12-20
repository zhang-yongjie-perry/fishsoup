package com.fishsoup.fishweb.controller;

import com.fishsoup.fishweb.domain.User;
import com.fishsoup.fishweb.enums.OnlineStatusEnum;
import com.fishsoup.fishweb.netty.NettyServer;
import com.fishsoup.fishweb.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.fishsoup.fishweb.netty.NettyServerHandler.chatClientMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

//    private final NettyServer nettyServer;
//
//    private final UserService userService;
//
//    @PostConstruct
//    public void init() {
//        new Thread(nettyServer).start();
//    }
//
//    @GetMapping("/user/list")
//    public List<User> listUsers() {
//        List<User> users = userService.listUsers();
//        users.forEach(user -> {
//            OnlineStatusEnum status = chatClientMap.containsKey(user.getUsername())
//                ? OnlineStatusEnum.ONLINE : OnlineStatusEnum.OFFLINE;
//            user.setOnlineStatus(status);
//        });
//        return users;
//    }
}
