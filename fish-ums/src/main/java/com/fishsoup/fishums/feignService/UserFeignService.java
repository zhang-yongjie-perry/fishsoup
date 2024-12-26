package com.fishsoup.fishums.feignService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.entity.user.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@FeignClient("fish-user")
public interface UserFeignService {

    @PostMapping("/user/auth/register")
    ResponseResult register(@RequestBody User user) throws BusinessException;

    @PostMapping("/user/auth/logout")
    ResponseResult logout(@RequestBody User user);

    @PostMapping("/user/save")
    ResponseResult saveUser(@RequestBody User user);

    @PostMapping("/user/batch/save")
    ResponseResult batchSaveUser(@RequestBody List<User> userList);

    @GetMapping("/user/list")
    List<User> listUsers();

    @GetMapping("/user/{username}")
    User findUserByUsername(@PathVariable("username") String username);

    @GetMapping("/user/chat/list")
    List<User> listChatUsers();

    @PostMapping("/user/update/password")
    ResponseResult updatePassword(@RequestBody User user) throws BusinessException;

    @PostMapping("/user/page/list")
    Page<User> pageUsers(@RequestBody User user, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize);
}
