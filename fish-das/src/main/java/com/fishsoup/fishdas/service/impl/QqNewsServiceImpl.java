package com.fishsoup.fishdas.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fishsoup.entity.news.HotNews;
import com.fishsoup.fishdas.service.HotNewsService;
import com.fishsoup.util.DateUtils;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service("QqNewsServiceImpl")
public class QqNewsServiceImpl implements HotNewsService {
    private static final String RAIN_BASE_URL = "https://news.qq.com/rain/a/";
    private static final String BASE_URL = "https://www.qq.com";
    private static final String ENTERTAINMENT_URL = "https://i.news.qq.com/gw/event/pc_hot_ranking_list?ids_hash=&offset=0&page_size=20&appver=15.5_qqnews_7.1.60&rank_id=ent";
    private static final String TECH_URL = "https://i.news.qq.com/web_feed/getPcPageList";
    private final static Headers HEADERS = new Headers.Builder().add("referer", BASE_URL).add("user-agent", USER_AGENT).build();
    private final static Headers JSON_HEADERS = new Headers.Builder().add("referer", BASE_URL)
        .add("content-type", "application/json; charset=utf-8")
        .add("priority", "u=1, i")
        .add("referer", BASE_URL)
        .add("user-agent", USER_AGENT).build();

    private final OkHttpClient okHttpClient;

    private final MongoTemplate mongoTemplate;

    @Override
    public boolean crawlHotNews() {
        Thread.startVirtualThread(() -> {
            String entertainmentResultHtml = request();
            if (!StringUtils.hasText(entertainmentResultHtml)) {
                return;
            }
            JSONObject entertainmentJsonObject = JSON.parseObject(entertainmentResultHtml);
            // 获取list中的title和url
            JSONArray list = (JSONArray) ((JSONObject) ((JSONArray) entertainmentJsonObject.get("idlist")).getFirst()).get("newslist");
            int seq = 0;
            for (Object l : list) {
                JSONObject ob = (JSONObject) l;
                if (ob.get("url") == null) {
                    continue;
                }
                Query query = new Query(Criteria.where("title").is(ob.get("title").toString())
                    .and("site").is("qq").and("news_type").is("1"));
                Update update = new Update();
                update.set("title", ob.get("title").toString());
                update.set("site", "qq");
                update.set("news_type", "1");
                update.set("href", ob.get("url").toString());
                update.set("seq", seq++);
                update.set("time", DateUtils.formatDate(DateUtils.now(), DateUtils.YYYY_MM_DD));
                update.set("create_time", DateUtils.now());
                Thread.startVirtualThread(() -> {
                    mongoTemplate.upsert(query, update, HotNews.class);
                });
            }
        });

        Thread.startVirtualThread(() -> {
            String techResultHtml = postRequest();
            JSONObject techResultJsonObject = JSON.parseObject(techResultHtml);
            JSONObject data = (JSONObject) ((JSONArray) techResultJsonObject.get("data")).get(0);
            // 获取id, 拼接 TRAN_URL 为完整请求路径, 获取 title
            JSONArray hotModule = (JSONArray) data.get("hot_module");
            int seq1 = 0;
            for (Object l : hotModule) {
                JSONObject ob = (JSONObject) l;
                Query query1 = new Query(Criteria.where("title").is(ob.get("title").toString())
                    .and("site").is("qq").and("news_type").is("2"));
                Update update1 = new Update();
                update1.set("title", ob.get("title").toString());
                update1.set("site", "qq");
                update1.set("news_type", "2");
                update1.set("href", RAIN_BASE_URL + ob.get("id").toString());
                update1.set("seq", seq1++);
                update1.set("time", DateUtils.formatDate(DateUtils.now(), DateUtils.YYYY_MM_DD));
                update1.set("create_time", DateUtils.now());
                Thread.startVirtualThread(() -> {
                    mongoTemplate.upsert(query1, update1, HotNews.class);
                });
            }

            JSONArray collectionArticle = (JSONArray) data.get("collection_article");
            for (Object l : collectionArticle) {
                JSONObject ob = (JSONObject) l;
                Query query2 = new Query(Criteria.where("title").is(ob.get("title").toString())
                    .and("site").is("qq").and("news_type").is("2"));
                Update update2 = new Update();
                update2.set("title", ob.get("title").toString());
                update2.set("site", "qq");
                update2.set("news_type", "2");
                update2.set("href", RAIN_BASE_URL + ob.get("id").toString());
                update2.set("seq", seq1++);
                update2.set("time", DateUtils.formatDate(DateUtils.now(), DateUtils.YYYY_MM_DD));
                update2.set("create_time", DateUtils.now());
                Thread.startVirtualThread(() -> {
                    mongoTemplate.upsert(query2, update2, HotNews.class);
                });
            }
        });
        return true;
    }

    private String request() {
        try (Response response = okHttpClient.newCall(new Request.Builder().url(ENTERTAINMENT_URL).headers(HEADERS).get().build()).execute()) {
            if (!response.isSuccessful()) {
                return "";
            }
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        } catch (IOException ioException) {
            log.error("请求{}异常: {}", BASE_URL, ioException.getMessage(), ioException);
            return "";
        }
    }

    private String postRequest() {
        JSONObject baseReq = new JSONObject();
        baseReq.put("from", "pc");
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("base_req", baseReq);
        jsonBody.put("channel_id", "news_news_tech");
        jsonBody.put("device_id", "0_RDewX1rhh5ZN9");
        jsonBody.put("flush_num", 1);
        jsonBody.put("forward", "2");
        jsonBody.put("qimei36", "0_RDewX1rhh5ZN9");
//        RequestBody requestBody = RequestBody.create(jsonBody.toJSONBBytes());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toJSONString());
        try (Response response = okHttpClient.newCall(new Request.Builder().url(TECH_URL).headers(JSON_HEADERS).post(requestBody).build()).execute()) {
            if (!response.isSuccessful()) {
                return "";
            }
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        } catch (IOException ioException) {
            log.error("请求{}异常: {}", BASE_URL, ioException.getMessage(), ioException);
            return "";
        }
    }
}
