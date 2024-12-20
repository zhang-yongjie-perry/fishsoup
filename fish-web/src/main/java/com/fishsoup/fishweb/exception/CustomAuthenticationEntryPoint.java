package com.fishsoup.fishweb.exception;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.fishweb.http.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // 可以返回JSON格式的错误信息或者调用你的错误处理接口
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 返回错误信息
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ResponseResult result = ResponseResult.error();
        switch (authException.getClass().getSimpleName()) {
            case "InsufficientAuthenticationException":
                result.setMsg("尚未登录, 请先登录");
                break;
            default:
                result.setMsg(authException.getMessage());
        }
        response.getWriter().write(JSON.toJSONString(result));
    }
}
