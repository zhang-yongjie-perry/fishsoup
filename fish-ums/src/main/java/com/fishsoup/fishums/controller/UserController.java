package com.fishsoup.fishums.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.entity.user.User;
import com.fishsoup.enums.ResponseCodeEnum;
import com.fishsoup.fishums.feignService.UserFeignService;
import com.fishsoup.fishums.service.UserService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserFeignService userFeignService;

    private final UserService userService;

    @ResponseBody
    @PostMapping
    public ResponseResult register(User user) throws BusinessException {
        userFeignService.register(user);
        return ResponseResult.success(user);
    }

    @GetMapping("/register")
    public String registerView() {
        return "user/register";
    }

    @GetMapping("/resetPassword")
    public String resetPasswordView() {
        return "user/resetPassword";
    }

    @ResponseBody
    @PostMapping("/update/password")
    public ResponseResult updatePassword(@RequestBody User user) throws BusinessException {
        User principal = (User) SecurityUtils.getSubject().getPrincipal();
        user.setUsername(principal.getUsername());
        ResponseResult responseResult = userFeignService.updatePassword(user);
        if (Objects.equals(ResponseCodeEnum.FAILURE.getCode(), responseResult.getCode())) {
            throw new BusinessException(responseResult.getMsg());
        }
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
        return userFeignService.pageUsers(user, page, limit);
    }

    @GetMapping("/currentUserInfo")
    public String currentUserInfoView(ModelMap map) {
        User currnetUser = (User) SecurityUtils.getSubject().getPrincipal();
        map.put("user", userFeignService.findUserByUsername(currnetUser.getUsername()));
        return "user/currentUserInfo";
    }

    @PostMapping("/update")
    public String saveUser(User user, ModelMap map) {
        userFeignService.saveUser(user);
        map.put("user", userFeignService.findUserByUsername(user.getUsername()));
        return "user/currentUserInfo";
    }

    /**
     * 用户锁定
     *
     * @param user 被锁定的用户, 需有userId/username
     * @return 操作结果
     */
    @ResponseBody
    @PostMapping("/lock")
    public ResponseResult lockAccount(@RequestBody User user) throws BusinessException {
        userService.lockAccount(user);
        return ResponseResult.success();
    }

    /**
     * 用户锁定
     *
     * @param userList 被锁定的用户, 需有username
     * @return 操作结果
     */
    @ResponseBody
    @PostMapping("/unlock")
    public ResponseResult unlockAccount(@RequestBody List<User> userList) throws BusinessException {
        userService.unlockAccount(userList);
        return ResponseResult.success();
    }
}
