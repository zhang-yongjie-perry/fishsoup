package com.fishsoup.fishweb.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fishsoup.entity.movie.TvMovie;
import com.fishsoup.fishweb.annotation.FootstepLog;
import com.fishsoup.fishweb.domain.Creation;
import com.fishsoup.fishweb.domain.Footstep;
import com.fishsoup.fishweb.enums.ArtworkTypeEnum;
import com.fishsoup.fishweb.enums.YesNoEnum;
import com.fishsoup.fishweb.mapper.FootstepMapper;
import com.fishsoup.fishweb.util.DateUtils;
import com.fishsoup.fishweb.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class FootstepAspect {

    private final FootstepMapper footstepMapper;

    private final ScheduledExecutorService scheduledExecutorService;

    @Pointcut("@annotation(com.fishsoup.fishweb.annotation.FootstepLog)")
    public void getPointCut(){}

    @Around("getPointCut()")
    public Object logFootstep(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        FootstepLog annotation = method.getAnnotation(FootstepLog.class);
        if (annotation == null) {
            return result;
        }
        String userId = SecurityUtils.getUserId();
        String loginName = SecurityUtils.getLoginName();
        scheduledExecutorService.schedule(() -> {
            ArtworkTypeEnum type = annotation.value();
            String correlationId = (String) joinPoint.getArgs()[0];
            String today = DateUtils.now(DateUtils.YYYY_MM_DD);
            QueryWrapper<Footstep> wrapper = new QueryWrapper<>();
            wrapper.eq("type", type.getCode());
            wrapper.eq("today", today);
            wrapper.eq("user_id", userId);
            wrapper.eq("correlation_id", correlationId);
            boolean exists = footstepMapper.exists(wrapper);
            if (exists) {
                footstepMapper.update(new Footstep().setUpdateTime(DateUtils.now()), wrapper);
                return;
            }
            Footstep footstep = new Footstep().setUserId(userId).setToday(today).setType(type).setCorrelationId(correlationId)
                .setDelFlag(YesNoEnum.NO)
                .setCreateBy(loginName).setCreateTime(DateUtils.now())
                .setUpdateBy(loginName).setUpdateTime(DateUtils.now());
            if (result instanceof Creation creation) {
                footstep.setTitle(creation.getTitle()).setAuthor(creation.getAuthor()).setSummary(creation.getSummary());
            } else if (result instanceof TvMovie movie) {
                footstep.setTitle(movie.getTitle()).setImageUrl(movie.getImgUrl())
                    .setPlayOrgName(movie.getPlayOrgs().getFirst().getOrgName())
                    .setEpisode(movie.getPlayOrgs().getFirst().getPlayList().getFirst().getEpisode())
                    .setM3u8Url(movie.getPlayOrgs().getFirst().getPlayList().getFirst().getM3u8Url());
            }
            footstepMapper.insert(footstep);
        }, 1L, TimeUnit.MICROSECONDS);
        return result;
    }
}
