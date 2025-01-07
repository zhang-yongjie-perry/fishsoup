package com.fishsoup.fishdas.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fishsoup.entity.news.HotNews;
import com.fishsoup.fishdas.network.OkHttpUtils;
import com.fishsoup.fishdas.service.HotNewsService;
import com.fishsoup.util.DateUtils;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import okhttp3.Headers;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("WeiboNewsServiceImpl")
public class WeiboNewsServiceImpl implements HotNewsService {

    public static final String REFERER_URL = "https://m.weibo.cn/p/106003type=25&amp;t=3&amp;disable_hot=1&amp;filter_type=realtimehot";
    public static final String REQUEST_URL = "https://m.weibo.cn/api/container/getIndex?containerid=106003type%3D25%26t%3D3%26disable_hot%3D1%26filter_type%3Drealtimehot&page_type=08";
    private final static Headers HEADERS = new Headers.Builder().add("referer", REFERER_URL).add("user-agent", USER_AGENT).build();

    private final OkHttpUtils httpUtils;

    private final MongoTemplate mongoTemplate;

    @Override
    public boolean crawlHotNews() {
        String response = httpUtils.doGetRequest(REQUEST_URL, HEADERS);
        if (!StringUtils.hasText(response)) {
            return false;
        }
        JSONObject responseJSONObj = JSON.parseObject(response);
        JSONArray dataArray = (JSONArray) ((JSONObject) responseJSONObj.get("data")).get("cards");
        JSONArray cardGroupArray = (JSONArray) ((JSONObject) dataArray.getFirst()).get("card_group");
        for (int i = 1; i < cardGroupArray.size(); i++) {
            JSONObject card = (JSONObject) cardGroupArray.get(i);
            String title = (String) card.get("desc");
            String src = (String) card.get("scheme");
            Query query = new Query(Criteria.where("title").is(title)
                .and("site").is("weibo").and("news_type").is("1"));
            Update update = new Update();
            update.set("title", title);
            update.set("site", "weibo");
            update.set("news_type", "1");
            update.set("href", src);
            update.set("seq", i);
            update.set("time", DateUtils.formatDate(DateUtils.now(), DateUtils.YYYY_MM_DD));
            update.set("create_time", DateUtils.now());
            Thread.startVirtualThread(() -> {
                mongoTemplate.upsert(query, update, HotNews.class);
            });
        }
        return true;
    }
}
