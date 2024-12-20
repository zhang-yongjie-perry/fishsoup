package com.fishsoup.fishweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.fishweb.domain.Footstep;
import com.fishsoup.fishweb.enums.ArtworkTypeEnum;

@SuppressWarnings("all")
public interface FootstepService {

    IPage<Footstep> pageFootsteps(ArtworkTypeEnum artworkType, int pageNum, int pageSize);

    boolean saveFootstep(Footstep footstep);
}
