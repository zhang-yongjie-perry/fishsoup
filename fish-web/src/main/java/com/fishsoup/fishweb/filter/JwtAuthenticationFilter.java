package com.fishsoup.fishweb.filter;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.fishweb.domain.User;
import com.fishsoup.fishweb.util.JWTUtils;
import com.fishsoup.fishweb.util.StringUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Objects;

import static com.fishsoup.fishweb.config.SecurityConfig.IGNORE_URLS;
import static com.fishsoup.fishweb.util.JWTUtils.ISSUER_FISH;


/**
 * 自定义jwt认证过滤器
 * 如果实现的是 Filter 接口, 将其自动注入到 spring 容器后, 为避免重复调用, 则需要禁用容器自动调用的功能
 * @Bean
 * public FilterRegistrationBean<JwtAuthenticationFilter> tenantFilterRegistration(JwtAuthenticationFilter filter) {
 *     FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
 *     registration.setEnabled(false);
 *     return registration;
 * }
 *
 * @author zhang_yjie
 * @2024-10-23
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 判断是否为忽略路径
        String servletPath = request.getServletPath();
        boolean skipAuth = Arrays.stream(IGNORE_URLS).anyMatch(url ->
            Objects.equals(url, servletPath) ? true : url.contains("/**")
                ? servletPath.startsWith(url.substring(0, url.indexOf("/**"))) : false
        );
        if (skipAuth) {
            filterChain.doFilter(request, response);
            return;
        }
        // 判断是否携带jwt, 若不携带则拒绝访问
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            throw new AccessDeniedException("未携带令牌, 权鉴认证失败");
        }
        String jwt = authorization.substring("Bearer ".length());
        Claims claims;
        try {
            claims = JWTUtils.parseJWT(jwt);
        } catch (Exception e) {
            throw new AccessDeniedException("访问令牌解析异常, 请重新登录");
        }
        if (!Objects.equals(ISSUER_FISH, claims.getIssuer())) {
            throw new AccessDeniedException("访问令牌签发者错误, 请重新登录");
        }

        String subject = claims.getSubject();
        // 为性能考虑此处的用户权限不进行数据库查询
        User user = JSON.parseObject(subject, User.class);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);
    }
}
