package com.fishsoup.fishweb.service;

import com.fishsoup.fishweb.domain.HotNews;

import java.util.List;

public interface HotNewsService {

    List<HotNews> listHotNews(HotNews conditions, int limit);
}
