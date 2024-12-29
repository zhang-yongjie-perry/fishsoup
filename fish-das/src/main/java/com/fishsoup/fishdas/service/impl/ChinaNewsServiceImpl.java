package com.fishsoup.fishdas.service.impl;

import com.fishsoup.entity.news.HotNews;
import com.fishsoup.fishdas.service.HotNewsService;
import com.fishsoup.util.DateUtils;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service("ChinaNewsServiceImpl")
public class ChinaNewsServiceImpl implements HotNewsService {

    public static final String BASE_URL = "https://www.chinanews.com.cn";

    private final OkHttpClient okHttpClient;

    private final MongoTemplate mongoTemplate;

    @Override
    public boolean crawlHotNews() {
        String searchResultHtml = request();
        if (!StringUtils.hasText(searchResultHtml)) {
            return false;
        }
        Document html = Jsoup.parse(searchResultHtml);
        Elements elements1 = html.getElementsByClass("ywjx-news-list");
        if (!elements1.isEmpty()) {
            Elements list = elements1.getFirst().getElementsByTag("a");
            if (!list.isEmpty()) {
                int seq = 0;
                for (Element element : list) {
                    if (element.attr("href").contains("iframe")) {
                        continue;
                    }
                    Query query = new Query(Criteria.where("title").is(element.text())
                        .and("site").is("ChinaNews").and("news_type").is("1"));
                    Update update = new Update();
                    update.set("title", element.text());
                    update.set("site", "ChinaNews");
                    update.set("news_type", "1");
                    update.set("href", BASE_URL + element.attr("href"));
                    update.set("seq", seq++);
                    update.set("time", DateUtils.formatDate(DateUtils.now(), DateUtils.YYYY_MM_DD));
                    update.set("create_time", DateUtils.now());
                    Thread.startVirtualThread(() -> {
                        mongoTemplate.upsert(query, update, HotNews.class);
                    });
                }
            }
        }


        Elements elements2 = html.getElementsByClass("rdph-list rdph-list2");
        if (!elements2.isEmpty()) {
            int seq = 0;
            for (Element element : elements2) {
                for (Element aEl : element.getElementsByTag("a")) {
                    Query query = new Query(Criteria.where("title").is(aEl.attr("title"))
                        .and("site").is("ChinaNews").and("news_type").is("2"));
                    Update update = new Update();
                    update.set("title", aEl.attr("title"));
                    update.set("site", "ChinaNews");
                    update.set("news_type", "2");
                    update.set("href", aEl.attr("href"));
                    update.set("seq", seq++);
                    update.set("time", DateUtils.formatDate(DateUtils.now(), DateUtils.YYYY_MM_DD));
                    update.set("create_time", DateUtils.now());
                    Thread.startVirtualThread(() -> {
                        mongoTemplate.upsert(query, update, HotNews.class);
                    });
                }
            }
        }
        return true;
    }

    private String request() {
        try (Response response = okHttpClient.newCall(new Request.Builder().url(BASE_URL).get().build()).execute()) {
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
