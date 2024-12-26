package com.fishsoup.fishuser.controller;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.entity.user.User;
import com.fishsoup.fishuser.service.UserService;
import com.fishsoup.util.EncryptUtil;
import com.fishsoup.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.fishsoup.fishuser.service.UserService.secretKeyMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @GetMapping("/publicKey")
    public ResponseResult getPublicKey() {
        KeyPair keyPair = EncryptUtil.generateSecretKeyPair();
        String publicKeyStr = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKeyStr = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        secretKeyMap.put(publicKeyStr, privateKeyStr);
        Map<String, String> map = new HashMap<>();
        map.put("publicKey", publicKeyStr);
        return ResponseResult.success(map);
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
    public ResponseResult login(@RequestBody User user, @RequestParam("publicKey") String publicKey) throws Exception {
        String privateKey = secretKeyMap.get(publicKey);
        if (privateKey == null) {
            throw new BusinessException("登录页面已过期, 请刷新页面重新登录");
        }
        user.setPassword(EncryptUtil.decrypt(user.getPassword(), EncryptUtil.getPrivateKey(privateKey)));
        User presentedUser = userService.login(user.getUsername(), user.getPassword());
        Map<String, Object> data = new HashMap<>();
        data.put("user", presentedUser);
        data.put("token", JWTUtils.createJWT(JSON.toJSONString(presentedUser)));
        return ResponseResult.success(data);
    }

    @PostMapping("/logout")
    public ResponseResult logout(@RequestBody User user) {
        return userService.logout(user) ? ResponseResult.success() : ResponseResult.error();
    }
}
