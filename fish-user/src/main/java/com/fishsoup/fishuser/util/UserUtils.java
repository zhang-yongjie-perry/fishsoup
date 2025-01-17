package com.fishsoup.fishuser.util;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.entity.user.User;
import com.fishsoup.util.JWTUtils;
import com.fishsoup.util.StringUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

import static com.fishsoup.constant.UserConstant.ADMIN;
import static com.fishsoup.util.JWTUtils.ISSUER_FISH;

public class UserUtils {

    public static String getAuthorization() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return ADMIN;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        return StringUtils.hasText(request.getHeader("Authorization")) ? request.getHeader("Authorization") : ADMIN;
    }

    public static User getLoginUser() {
        if (Objects.equals(getAuthorization(), ADMIN)) {
            return null;
        }
        String jwt = getAuthorization().substring("Bearer ".length());
        Claims claims;
        try {
            claims = JWTUtils.parseJWT(jwt);
        } catch (Exception e) {
            throw new RuntimeException("访问令牌解析异常, 请重新登录");
        }
        if (!Objects.equals(ISSUER_FISH, claims.getIssuer())) {
            throw new RuntimeException("访问令牌签发者错误, 请重新登录");
        }

        String subject = claims.getSubject();
        return JSON.parseObject(subject, User.class);
    }

    public static String getLoginName() {
        User loginUser = getLoginUser();
        return Objects.isNull(loginUser) ? ADMIN : loginUser.getUsername();
    }

    public static String getUserId() {
        User loginUser = getLoginUser();
        return Objects.isNull(loginUser) ? ADMIN : loginUser.getId();
    }
}
