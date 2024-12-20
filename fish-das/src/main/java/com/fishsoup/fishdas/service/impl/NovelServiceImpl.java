package com.fishsoup.fishdas.service.impl;

import com.fishsoup.fishdas.network.OkHttpUtils;
import com.fishsoup.fishdas.service.NovelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.fishsoup.fishdas.network.OkHttpUtils.USER_AGENT;


@Slf4j
@Service
@RequiredArgsConstructor
public class NovelServiceImpl implements NovelService {

    public final static String REFERER = "https://www.mianfeixiaoshuoyueduwang.com/";
    public final static String SEARCH_PAGE = "https://www.mianfeixiaoshuoyueduwang.com/?c=book&a=search&keywords=%s";
    public final static Headers HEADERS = new Headers.Builder().add("referer", REFERER).add("user-agent", USER_AGENT).build();

    private final OkHttpUtils httpUtils;

    @Override
    public boolean crawlNovelsByName(String novelName) {
        String url = String.format(SEARCH_PAGE, novelName);
        Map<String, String> params = new HashMap<>();
        params.put("searchkey", novelName);
        String response = httpUtils.get(url, HEADERS, null);
        log.info("响应: {}", response);
        return true;
    }
}
