package com.fishsoup.fishdas.service;

import com.fishsoup.fishdas.network.OkHttpUtils;

@SuppressWarnings("all")
public interface NovelService {
    boolean crawlNovelsByName(String novelName);
}
