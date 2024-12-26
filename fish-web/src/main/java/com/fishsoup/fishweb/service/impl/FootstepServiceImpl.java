package com.fishsoup.fishweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishsoup.fishweb.domain.Footstep;
import com.fishsoup.fishweb.enums.ArtworkTypeEnum;
import com.fishsoup.fishweb.mapper.FootstepMapper;
import com.fishsoup.fishweb.service.FootstepService;
import com.fishsoup.fishweb.util.UserUtils;
import com.fishsoup.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FootstepServiceImpl implements FootstepService {

    private final FootstepMapper footstepMapper;

    @Override
    public IPage<Footstep> pageFootsteps(ArtworkTypeEnum artworkType, int pageNum, int pageSize) {
        IPage<Footstep> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Footstep> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", UserUtils.getUserId());
        queryWrapper.eq("type", artworkType.getCode());
        queryWrapper.orderByDesc("today", "update_time");
        return footstepMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean saveFootstep(Footstep footstep) {
        QueryWrapper<Footstep> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", UserUtils.getUserId());
        queryWrapper.eq("type", footstep.getType());
        queryWrapper.eq("today", DateUtils.now(DateUtils.YYYY_MM_DD));
        queryWrapper.eq("correlation_id", footstep.getCorrelationId());
        Footstep toUpdate = new Footstep().setPlayOrgName(footstep.getPlayOrgName()).setEpisode(footstep.getEpisode()).setM3u8Url(footstep.getM3u8Url())
            .setUpdateBy(UserUtils.getLoginName()).setUpdateTime(DateUtils.now());
        footstepMapper.update(toUpdate, queryWrapper);
        return true;
    }
}
