package com.fishsoup.fishdas.service;

import okhttp3.Headers;

import static com.fishsoup.fishdas.network.OkHttpUtils.USER_AGENT;

@SuppressWarnings("all")
public interface PictureService {

    public final static String REFERER = "https://www.bizhihui.com/tags/8Kbizhi/";
    public final static String SEARCH_PAGE_8K = "https://www.bizhihui.com/tags/8Kbizhi/";
    public final static String SEARCH_PAGE_4k = "https://www.bizhihui.com/tags/4Kbizhi/";
    public final static Headers HEADERS = new Headers.Builder().add("referer", REFERER).add("user-agent", USER_AGENT).build();
    public final static String RUL_SUFFIX = "-pcthumbs";

    boolean crawl8kPic(int pageNum);

    boolean crawl4kPic(int pageNum);
}
