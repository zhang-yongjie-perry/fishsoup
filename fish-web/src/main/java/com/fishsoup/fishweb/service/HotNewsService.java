package com.fishsoup.fishweb.service;

import com.fishsoup.entity.news.HotNews;

import java.util.List;

public interface HotNewsService {

    List<HotNews> listHotNews(HotNews conditions, int page, int limit);
}
