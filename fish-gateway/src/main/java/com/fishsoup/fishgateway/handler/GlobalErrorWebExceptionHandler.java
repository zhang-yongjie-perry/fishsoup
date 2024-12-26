package com.fishsoup.fishgateway.handler;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.entity.http.ResponseResult;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@SuppressWarnings("all")
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof AccessDeniedException) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
        }
        DataBuffer wrap = exchange.getResponse().bufferFactory()
            .wrap(JSON.toJSONString(ResponseResult.error(ex.getMessage())).getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(wrap));
    }
}
