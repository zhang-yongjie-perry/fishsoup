package com.fishsoup.fishuser.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.entity.user.User;
import com.fishsoup.enums.OnlineStatusEnum;
import com.fishsoup.fishuser.service.UserService;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final RedissonClient redissonClient;

    @PostMapping("/save")
    public ResponseResult saveUser(@RequestBody User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", userService.saveUser(user));
        return ResponseResult.success(data);
    }

    @GetMapping("/list")
    public List<User> listUsers() {
        return userService.listUsers();
    }

    @GetMapping("/{username}")
    public User findUserByUsername(@PathVariable("username") String username) {
        return userService.selectOneByUsername(username);
    }

    @GetMapping("/chat/list")
    public List<User> listChatUsers() {
        List<User> users = userService.listChatUsers();
        users.forEach(user -> {
            RBucket<Integer> bucket = redissonClient.getBucket("fish:chat:" + user.getUsername());
            OnlineStatusEnum status = bucket.get() != null && bucket.get() > 0 ? OnlineStatusEnum.ONLINE : OnlineStatusEnum.OFFLINE;
            user.setOnlineStatus(status);
        });
        return users;
    }

    @PostMapping("/update/password")
    public ResponseResult updatePassword(@RequestBody User user) throws BusinessException {
        userService.updatePassword(user);
        return ResponseResult.success();
    }

    @PostMapping("/page/list")
    public IPage<User> pageUsers(@RequestBody User user, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        return userService.pageUsers(user, pageNum, pageSize);
    }

    @PostMapping("/batch/save")
    public ResponseResult batchSaveUser(@RequestBody List<User> userList) {
        userService.batchSaveUser(userList);
        return ResponseResult.success();
    }
}
