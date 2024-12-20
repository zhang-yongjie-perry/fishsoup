package com.fishsoup.fishchat.exception;

import com.fishsoup.fishchat.http.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ResponseBody
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseResult businessException(BusinessException e) {
        log.error("请求异常: {}", e.getMessage(), e);
        return ResponseResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult allException(Exception e) {
        return ResponseResult.error(e.getMessage());
    }
}