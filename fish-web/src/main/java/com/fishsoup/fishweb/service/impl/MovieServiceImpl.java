package com.fishsoup.fishweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishsoup.entity.movie.TvMovie;
import com.fishsoup.fishweb.annotation.FootstepLog;
import com.fishsoup.fishweb.domain.Footstep;
import com.fishsoup.fishweb.exception.BusinessException;
import com.fishsoup.fishweb.feignService.DasFeignService;
import com.fishsoup.fishweb.mapper.FootstepMapper;
import com.fishsoup.fishweb.service.MovieService;
import com.fishsoup.fishweb.util.DateUtils;
import com.fishsoup.fishweb.util.SecurityUtils;
import com.fishsoup.fishweb.util.StringUtils;
import com.fishsoup.utils.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.fishsoup.fishweb.enums.ArtworkTypeEnum.MOVIE;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MongoTemplate mongoTemplate;

    private final DasFeignService dasFeignService;

    private final FootstepMapper footstepMapper;

    @Override
    public String helloDas() {
        return dasFeignService.helloDas();
    }

    @Override
    public IPage<TvMovie> pageTvMovies(TvMovie conditions, int pageNum, int pageSize) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(conditions) && StringUtils.hasText(conditions.getTitle())) {
            criteria.and("title").regex(Pattern.compile("^.*" + conditions.getTitle() + ".*$", Pattern.CASE_INSENSITIVE));
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
    public TvMovie findTvMovieById(String id) throws BusinessException {
        TvMovie mv = mongoTemplate.findById(id, TvMovie.class);
        if (mv == null) {
            return null;
        }
        if (mv.getStatus() == 1) {
            QueryWrapper<Footstep> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", SecurityUtils.getUserId());
            queryWrapper.eq("type", MOVIE.getCode());
            queryWrapper.eq("today", DateUtils.now(DateUtils.YYYY_MM_DD));
            queryWrapper.eq("correlation_id", id);
            List<Footstep> footsteps = footstepMapper.selectList(queryWrapper);
            if (CollectionUtils.isEmpty(footsteps)) {
                return mv;
            }
            return mv.setHisPlayOrgName(footsteps.getFirst().getPlayOrgName()).setHisEpisode(footsteps.getFirst().getEpisode())
                .setHisM3u8Url(footsteps.getFirst().getM3u8Url());
        }
        // 通知fish-das去获取剧集信息
        boolean success = dasFeignService.crawlEpisodesByMovieId(id);
        if (!success) {
            throw new BusinessException("剧集信息获取失败, 请联系管理员");
        }
        return mongoTemplate.findById(id, TvMovie.class);
    }

    @Override
    public boolean searchTvMovieByTitle(String title) throws BusinessException {
        boolean success= dasFeignService.crawlMoviesByName(title);
        if (!success) {
            throw new BusinessException("视频获取失败, 请联系管理员");
        }
        return true;
    }
}
