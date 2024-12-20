package com.fishsoup.fishweb.controller;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.fishweb.domain.User;
import com.fishsoup.fishweb.exception.BusinessException;
import com.fishsoup.fishweb.http.ResponseResult;
import com.fishsoup.fishweb.service.UserService;
import com.fishsoup.fishweb.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    public ThreadLocal<Integer> localInteger = new ThreadLocal<>();

    @GetMapping(path = "/hello")
    public ResponseResult helloFish() {
        return ResponseResult.success("hello fish");
    }

    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user) throws BusinessException {
        userService.register(user);
        return ResponseResult.success();
    }

    /**
     * json参数格式的登录请求
     *
     * @param user 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user) {
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(user.getUsername(), user.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);

        Map<String, Object> data = new HashMap<>();
        data.put("user", authentication.getPrincipal());
        data.put("token", JWTUtils.createJWT(JSON.toJSONString(authentication.getPrincipal())));
        return ResponseResult.success(data);
    }

    @PostMapping("/logout")
    public ResponseResult logout(@RequestBody User user) {
        return userService.logout(user) ? ResponseResult.success() : ResponseResult.error();
    }

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
}
