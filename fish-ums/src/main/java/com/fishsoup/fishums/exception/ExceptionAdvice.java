package com.fishsoup.fishums.exception;

import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.http.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

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
        if (Objects.equals(e.getMessage(), "try to proceed invocation error")) {
            return ResponseResult.error(e.getCause().getMessage());
        }
        return ResponseResult.error(e.getMessage());
    }
}
