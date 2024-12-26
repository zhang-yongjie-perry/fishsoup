package com.fishsoup.fishgateway.filter;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.entity.user.User;
import com.fishsoup.util.JWTUtils;
import com.fishsoup.util.StringUtils;
import io.jsonwebtoken.Claims;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

import static com.fishsoup.util.JWTUtils.ISSUER_FISH;


/**
 * 自定义jwt认证过滤器
 *
 * @author zhang_yjie
 * @date 2024-12-20
 */
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final RedissonClient redissonClient;

    public JwtAuthenticationFilter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public static String[] IGNORE_URLS = {"/user/auth/**", "/web/image/download/**"};

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        boolean skipAuth = Arrays.stream(IGNORE_URLS).anyMatch(url ->
            Objects.equals(url, path) || (url.contains("/**") && path.startsWith(url.substring(0, url.indexOf("/**"))))
        );
        if (skipAuth) {
            return chain.filter(exchange);
        }
        // 判断是否携带jwt, 若不携带则拒绝访问
        String authorization = Objects.equals("/chat/ws", path) ? request.getQueryParams().getFirst("Authorization")
            : request.getHeaders().getFirst("Authorization");
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return Mono.error(new AccessDeniedException("未携带令牌, 权鉴认证失败"));
        }
        String jwt = authorization.substring("Bearer ".length());
        Claims claims;
        try {
            // 令牌必须可以解析, 否则视为无效
            claims = JWTUtils.parseJWT(jwt);
        } catch (Exception e) {

            return Mono.error(new AccessDeniedException("访问令牌解析异常, 请重新登录"));
        }
        if (!Objects.equals(ISSUER_FISH, claims.getIssuer())) {
            return Mono.error(new AccessDeniedException("访问令牌签发者错误, 请重新登录"));
        }

        String subject = claims.getSubject();
        User user = JSON.parseObject(subject, User.class);
        // 从redis获取用户缓存信息
        RBucket<User> bucket = redissonClient.getBucket("fish:user:" + user.getUsername());
        User cacheUser =  bucket.get();
        if (cacheUser == null) {
            return Mono.error(new AccessDeniedException("登录过期, 请重新登录"));
        }
        // 延迟2个小时
        bucket.expire(Duration.ofHours(2));
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
