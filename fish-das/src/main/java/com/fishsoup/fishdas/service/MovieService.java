package com.fishsoup.fishdas.service;

@SuppressWarnings("all")
public interface MovieService {

    /**
     * 根据名字搜索剧集
     *
     * @param title 搜索名称
     * @return 是否成功
     */
    boolean crawlMoviesByName(String title);

    /**
     * 根据数据库中的id对剧集进行搜索
     * @param id
     * @return
     */
    boolean crawlEpisodesByMovieId(String id);
}
