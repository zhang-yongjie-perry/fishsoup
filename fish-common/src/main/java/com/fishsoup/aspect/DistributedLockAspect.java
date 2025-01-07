package com.fishsoup.aspect;

import com.fishsoup.annotation.DistributedLock;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.util.MD5Util;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    @Pointcut("@annotation(com.fishsoup.annotation.DistributedLock)")
    public void getPointCut(){}

    @Around("getPointCut()")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);
        if (annotation == null) {
            return joinPoint.proceed();
        }
        Object[] args = joinPoint.getArgs();
        String name = annotation.name();
        int waitTime = annotation.waitTime();
        int leaseTime = annotation.leaseTime();
        StringBuilder argStr = new StringBuilder();
        for (Object arg : args) {
            argStr.append(arg.toString());
        }
        String md5Hash = MD5Util.getMD5Hash(argStr.toString());
        String lockName = name.concat(StringUtils.PACKAGE_COLON_CHAR).concat(md5Hash);
        RLock fairLock = redissonClient.getFairLock(lockName);
        boolean success = fairLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        if (!success) {
            throw new BusinessException("请求重复, 请1小时后再试");
        }
        try {
            return joinPoint.proceed();
        } finally {
            if (annotation.leaseTime() == -1 && fairLock.isLocked()) {
                fairLock.unlock();
            }
        }
    }
}
