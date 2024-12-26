package com.fishsoup.fishweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.movie.TvMovie;

@SuppressWarnings("all")
public interface MovieService {

    String helloDas();

    IPage<TvMovie> pageTvMovies(TvMovie conditions, int pageNum, int pageSize);

    TvMovie findTvMovieById(String id) throws BusinessException;

    boolean searchTvMovieByTitle(String title) throws BusinessException;
}
