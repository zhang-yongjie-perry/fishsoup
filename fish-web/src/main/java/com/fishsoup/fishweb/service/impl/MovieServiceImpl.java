package com.fishsoup.fishweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.entity.movie.TvMovie;
import com.fishsoup.fishweb.annotation.FootstepLog;
import com.fishsoup.fishweb.domain.Footstep;
import com.fishsoup.fishweb.feignService.DasFeignService;
import com.fishsoup.fishweb.mapper.FootstepMapper;
import com.fishsoup.fishweb.service.MovieService;
import com.fishsoup.fishweb.util.UserUtils;
import com.fishsoup.util.CollectionUtils;
import com.fishsoup.util.DateUtils;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.fishsoup.enums.MovieStatusEnum.COMPLETED;
import static com.fishsoup.enums.ResponseCodeEnum.FAILURE;
import static com.fishsoup.fishweb.enums.ArtworkTypeEnum.MOVIE;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MongoTemplate mongoTemplate;

    private final DasFeignService dasFeignService;

    private final FootstepMapper footstepMapper;

    private final RedissonClient redisson;

    @Override
    public IPage<TvMovie> pageTvMovies(TvMovie conditions, int pageNum, int pageSize) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(conditions) && StringUtils.hasText(conditions.getTitle())) {
            criteria.and("title").regex(Pattern.compile("^.*" + conditions.getTitle() + ".*$", Pattern.CASE_INSENSITIVE));
        }
        if (Objects.nonNull(conditions) && StringUtils.hasText(conditions.getSite())) {
            criteria.and("site").is(conditions.getSite());
        }
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Order.asc("sort_num"), Sort.Order.desc("last_update_time")));
        query.fields().include("_id", "title", "img_url", "synopsis", "last_update_time", "status", "sort_num");

        // 总数量
        long total = mongoTemplate.count(query, TvMovie.class);
        IPage<TvMovie> page = new Page<>(pageNum, pageSize, total);

        // 分页
        query = query.with(PageRequest.of(pageNum - 1, pageSize));
        page.setRecords(mongoTemplate.find(query, TvMovie.class));
        return page;
    }

    @Override
    @FootstepLog(MOVIE)
    public TvMovie findTvMovieById(String id) {
        TvMovie mv = mongoTemplate.findById(id, TvMovie.class);
        if (mv == null) {
            return null;
        }
        if (Objects.equals(mv.getStatus(), COMPLETED.getCode())) {
            QueryWrapper<Footstep> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", UserUtils.getUserId());
            queryWrapper.eq("type", MOVIE.getCode());
            queryWrapper.eq("today", DateUtils.now(DateUtils.YYYY_MM_DD));
            queryWrapper.eq("correlation_id", id);
            List<Footstep> footsteps = footstepMapper.selectList(queryWrapper);
            if (CollectionUtils.isEmpty(footsteps)) {
                return mv;
            }
            return mv.setHisPlayOrgName(footsteps.getFirst().getPlayOrgName())
                .setHisEpisode(footsteps.getFirst().getEpisode())
                .setHisM3u8Url(footsteps.getFirst().getM3u8Url())
                .setStartTime(footsteps.getFirst().getStartTime());
        }
        // 通知fish-das去获取剧集信息
        ResponseResult responseResult = dasFeignService.crawlEpisodesByMovieId(id);
        if (Objects.isNull(responseResult)) {
            throw new BusinessException("请求超时, 请稍后再试");
        }
        if (Objects.equals(responseResult.getCode(), FAILURE.getCode())) {
            throw new BusinessException(responseResult.getMsg());
        }
        return mongoTemplate.findById(id, TvMovie.class);
    }

    @Override
    public boolean searchTvMovieByTitle(String title) {
        RBucket<Integer> bucket = redisson.getBucket("crawlMv:" + UserUtils.getLoginName());
        Integer crawlNum = bucket.get();
        if (crawlNum == null) {
            bucket.set(5);
            bucket.expire(Duration.ofSeconds(DateUtils.getSameDayExpirationTime()));
            crawlNum = bucket.get();
        }
        if (crawlNum == 0) {
            throw new BusinessException("今日搜索次数(5次)已用完");
        }
        ResponseResult responseResult = dasFeignService.crawlMoviesByName(title);
        if (Objects.isNull(responseResult)) {
            throw new BusinessException("请求超时, 请稍后再试");
        }
        if (Objects.equals(responseResult.getCode(), FAILURE.getCode())) {
            throw new BusinessException(responseResult.getMsg());
        }
        bucket.set(--crawlNum);
        return true;
    }

    @Override
    public boolean searchNunuTvMovieByTitle(String title) {
        RBucket<Integer> bucket = redisson.getBucket("crawlNunuMv:" + UserUtils.getLoginName());
        Integer crawlNum = bucket.get();
        if (crawlNum == null) {
            bucket.set(5);
            bucket.expire(Duration.ofSeconds(DateUtils.getSameDayExpirationTime()));
            crawlNum = bucket.get();
        }
        if (crawlNum == 0) {
            throw new BusinessException("今日搜索次数(5次)已用完");
        }
        ResponseResult responseResult = dasFeignService.crawlNunuMoviesByName(title);
        if (Objects.isNull(responseResult)) {
            throw new BusinessException("请求超时, 请稍后再试");
        }
        if (Objects.equals(responseResult.getCode(), FAILURE.getCode())) {
            throw new BusinessException(responseResult.getMsg());
        }
        bucket.set(--crawlNum);
        return true;
    }

    @Override
    public String getNunuM3u8Resource(String sourceId) {
        ResponseResult responseResult = dasFeignService.crawlNunuM3u8Source(sourceId);
        if (Objects.isNull(responseResult)) {
            throw new BusinessException("请求超时, 请稍后再试");
        }
        if (Objects.equals(responseResult.getCode(), FAILURE.getCode())) {
            throw new BusinessException(responseResult.getMsg());
        }
        return responseResult.getData().toString();
    }
}
