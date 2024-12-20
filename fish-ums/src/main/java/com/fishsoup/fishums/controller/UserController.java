package com.fishsoup.fishums.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.fishums.domain.User;
import com.fishsoup.fishums.exception.BusinessException;
import com.fishsoup.fishums.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }

    @ResponseBody
    @PostMapping
    public ResponseResult register(User user) throws BusinessException {
        userService.register(user);
        return ResponseResult.success(user);
    }

    @GetMapping("/register")
    public String registerView() {
        return "user/register";
    }

    @ResponseBody
    @PostMapping("/update/password")
    public ResponseResult updatePassword(@RequestBody String newPassword) {
        userService.updatePassword(newPassword);
        return ResponseResult.success();
    }

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            return "redirect:home";
        }
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(true);
        currentUser.login(token);
        return "redirect:home";
    }

    @GetMapping("/home")
    public String homeView(ModelMap map) {
        map.put("user", SecurityUtils.getSubject().getPrincipal());
        return "home";
    }

    @GetMapping("/logout")
    public String logout() {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return "redirect:login";
    }

    @GetMapping("/view/list")
    public String userListView(ModelMap map) {
        map.put("user", SecurityUtils.getSubject().getPrincipal());
        return "user/userList";
    }

    @ResponseBody
    @PostMapping("/list")
    public IPage<User> pageUsers(User user, @RequestParam("page") int page, @RequestParam("limit") int limit) {
        return userService.pageUsers(user, page, limit);
    }

    @GetMapping("/currentUserInfo")
    public String currentUserInfoView(ModelMap map) throws BusinessException {
        User currnetUser = (User) SecurityUtils.getSubject().getPrincipal();
        map.put("user", userService.selectOneByUsername(currnetUser.getUsername()));
        return "user/currentUserInfo";
    }

    @PostMapping("/update")
    public String saveUser(User user, ModelMap map) throws BusinessException {
        userService.updateUser(user);
        map.put("user", userService.selectOneByUsername(user.getUsername()));
        return "user/currentUserInfo";
    }

    @ResponseBody
    @GetMapping("/test/arrayList/{num}")
    public ResponseResult testArrayList(@PathVariable("num") Long num) {
        long cost = userService.testArrayList(num);
        return ResponseResult.success(cost);
    }

    @ResponseBody
    @GetMapping("/test/linkedList/{num}")
    public ResponseResult testLinkedList(@PathVariable("num") Long num) {
        long cost = userService.testLinkedList(num);
        return ResponseResult.success(cost);
    }
}
