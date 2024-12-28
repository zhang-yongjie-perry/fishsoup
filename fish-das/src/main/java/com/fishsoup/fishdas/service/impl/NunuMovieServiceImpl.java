package com.fishsoup.fishdas.service.impl;

import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.movie.MvResource;
import com.fishsoup.entity.movie.PlayMovie;
import com.fishsoup.entity.movie.TvMovie;
import com.fishsoup.enums.MovieStatusEnum;
import com.fishsoup.fishdas.service.MovieService;
import com.fishsoup.util.CollectionUtils;
import com.fishsoup.util.DateUtils;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 努努影视爬虫
 */
@Slf4j
@Service("NunuMovieServiceImpl")
@RequiredArgsConstructor
public class NunuMovieServiceImpl implements MovieService {

    private final static String REFERER_URL = "https://www.nunuys.com/";
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
    private final static String HTTP_REGEX = "http.*?\\.m3u8";
    private final static String SOURCE_REGEX = "var urlList = decryptDict.*?\\$\\(document\\)\\.ready\\(function\\(\\)\\{";
    private final static Headers HEADERS = new Headers.Builder().add("referer", REFERER_URL).add("user-agent", USER_AGENT).build();

    private final OkHttpClient okHttpClient;

    private final MongoTemplate mongoTemplate;

    @Override
    public boolean crawlMoviesByName(String title) {
        String requestUrl = String.format("https://www.nunuys.com/search?q=%s", title);
        // 1. 解析搜索结果列表
        List<TvMovie> mvs = Collections.synchronizedList(new ArrayList<>());
        toParseSearchResultHtml(requestUrl, mvs);
        // 2. 解析播放首页页
        if (CollectionUtils.isEmpty(mvs)) {
            return false;
        }

        // 解析播放首页
        toParsePlayHomeUrl(mvs);

        // 保存数据
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            mvs.forEach(mv -> {
                executorService.submit(() -> {
                    saveMv(mv);
                });
            });
        }
        return true;
    }

    public void saveMv(TvMovie mv) {
        Query query = new Query(Criteria.where("title").is(mv.getTitle()).and("play_home_url").is(mv.getPlayHomeUrl()));
        Update update = new Update();
        update.set("title", mv.getTitle());
        update.set("site", mv.getSite());
        update.set("sort_num", mv.getSortNum());
        update.set("img_url", mv.getImgUrl());
        update.set("synopsis", mv.getSynopsis());
        update.set("play_home_url", mv.getPlayHomeUrl());
        update.set("status", mv.getStatus());
        update.set("last_update_time", mv.getLastUpdateTime());
        update.set("play_orgs", mv.getPlayOrgs());
        update.set("episode_text", mv.getEpisodeText());
        mongoTemplate.upsert(query, update, TvMovie.class);
    }

    @Override
    public boolean crawlEpisodesByMovieId(String id) {
        return true;
    }

    /**
     * 解析播放地址
     * @param mvs 剧集列表
     */
    private void topParsePlayUrl(List<TvMovie> mvs) {
    }

    /**
     * 对url发起get请求
     *
     * @param url 请求地址
     * @return 请求结果
     */
    private String request(String url) {
        if (!url.startsWith("http")) {
            url = REFERER_URL + url;
        }

        try (Response response = okHttpClient.newCall(new Request.Builder().headers(HEADERS).url(url).get().build()).execute()) {
            if (!response.isSuccessful()) {
                return "";
            }
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        } catch (IOException ioException) {
            log.error("请求{}异常: {}", url, ioException.getMessage(), ioException);
            return "";
        }
    }

    @Override
    public String getM3u8Resource(String resourceId) {
        MvResource mr = mongoTemplate.findOne(new Query(Criteria.where("source_id").is(resourceId)), MvResource.class);
        if (mr != null && StringUtils.hasText(mr.getM3u8Url())) {
            return mr.getM3u8Url();
        }
        MvResource mvResource = new MvResource().setSite("nunu").setCreateTime(DateUtils.now()).setSourceId(resourceId);
        String m3u8UrlRes = "";
        FormBody formBody = new FormBody.Builder().add("id", resourceId).build();
        try (Response response = okHttpClient.newCall(new Request.Builder()
            .headers(new Headers.Builder()
                .add(":authority", "www.nunuys.com")
                .add(":method", "POST")
                .add(":path", "/source/")
                .add(":scheme", "https")
                .add("Accept", "*/*")
                .add("Accept-encoding", "gzip, deflate, br, zstd")
                .add("Accept-language", "zh-CN,zh;q=0.9")
                .add("Content-length", "11")
                .add("Content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                .add("Origin", "https://www.nunuys.com")
                .add("Priority", "u=0, i")
                .add("Sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
                .add("Sec-ch-ua-mobile", "?0")
                .add("Referer", "https://www.nunuys.com/dsj/76668")
                .add("Sec-ch-ua-platform", "\"Windows\"")
                .add("Sec-fetch-dest", "empty")
                .add("Sec-fetch-mode", "cors")
                .add("Sec-fetch-site", "same-origin")
                .add("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .add("X-requested-with", "XMLHttpRequest")
                .build()).url("https://www.nunuys.com/source/")
            .post(formBody).build()).execute()) {
            if (!response.isSuccessful()) {
                throw new BusinessException("获取资源失败");
            }
            if (response.body() == null) {
                throw new BusinessException("获取资源失败");
            }
            m3u8UrlRes = response.body().string();
        } catch (IOException ioException) {
            log.error("请求{}异常: {}", "https://www.nunuys.com/source/", ioException.getMessage(), ioException);
            throw new BusinessException("获取资源失败");
        }
        Matcher matcher = Pattern.compile(HTTP_REGEX, Pattern.CASE_INSENSITIVE).matcher(m3u8UrlRes);
        if (!matcher.find()) {
            throw new BusinessException("获取资源失败");
        }
        mvResource.setM3u8Url(matcher.group(0).trim());
        mongoTemplate.save(mvResource);
        return mvResource.getM3u8Url();
    }

    /**
     * 解析搜索页
     * @param searchUrl 搜索地址
     * @param mvs 剧集列表
     * @return 搜索页内容
     */
    private Document doParseSearchResultHtml(String searchUrl, List<TvMovie> mvs) {
        String searchResultHtml = request(searchUrl);
        if (!StringUtils.hasText(searchResultHtml)) {
            return null;
        }

        // 播放列表
        Document html = Jsoup.parse(searchResultHtml);
        Elements playListEls = html.getElementsByClass("lists-content").getFirst().getElementsByTag("li");
        List<TvMovie> resultMvs = playListEls.parallelStream().map(element -> {
            Element infoEl = element.getElementsByTag("a").getFirst();
            Elements imgEl = infoEl.getElementsByTag("img");
            String imgUrl = imgEl.attr("src");
            String title = imgEl.attr("alt");
            String detailsUrl = REFERER_URL + infoEl.attr("href");
            Elements notes = infoEl.getElementsByClass("note");
            String synopsis = "";
            if (!notes.isEmpty()) {
                Element spanEl = notes.first();
                if (spanEl != null) {
                    synopsis = spanEl.getElementsByTag("span").text();
                }
            }
            return new TvMovie().setTitle(title)
                .setSite("nunu")
                .setStatus(MovieStatusEnum.COMPLETED.getCode())
                .setSortNum(0)
                .setImgUrl(imgUrl)
                .setPlayHomeUrl(detailsUrl)
                .setSynopsis(synopsis)
                .setLastUpdateTime(DateUtils.now());
        }).toList();
        mvs.addAll(resultMvs);
        return html;
    }

    /**
     * 根据搜索页内容, 填满集合mvs
     * 本地测试: searchResultHtml = Files.readString(Path.of("content.txt"));
     *
     * @param searchUrl 搜索地址
     * @param mvs 搜索内容
     */
    private void toParseSearchResultHtml(String searchUrl, List<TvMovie> mvs) {
        Document html =  doParseSearchResultHtml(searchUrl, mvs);
        if (html == null) {
            return;
        }

        // 分页列表
        Elements pageListEls = html.getElementsByClass("next-page");
        if (pageListEls.isEmpty()) {
            return;
        }
        Elements aTagEls = pageListEls.getFirst().getElementsByTag("a");
        int size = aTagEls.size();
        if (size < 1) {
            return;
        }
        String lastUrl = Objects.equals("下一页", aTagEls.get(size - 1).text()) ? aTagEls.get(size - 1).attr("href") : "";
        if (!StringUtils.hasText(lastUrl)) {
            return;
        }
        toParseSearchResultHtml(REFERER_URL + "search" + lastUrl, mvs);
    }

    /**
     * 解析播放主页
     *
     * @param mvs 剧集列表
     */
    private void toParsePlayHomeUrl(List<TvMovie> mvs) {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            mvs.forEach(mv -> {
                executorService.submit(() -> {
                    String request = request(mv.getPlayHomeUrl());
                    if (!StringUtils.hasText(request)) {
                        return;
                    }
                    // 这里需要匹配换行 Pattern.DOTALL
                    Matcher matcher = Pattern.compile(SOURCE_REGEX, Pattern.DOTALL).matcher(request);
                    if (!matcher.find()) {
                        return;
                    }
                    String urlList = matcher.group(0).trim().replaceAll("var urlList = ", "")
                        .replaceAll("\\$\\(document\\)\\.ready\\(function\\(\\)\\{", "").trim();
                    mv.setEpisodeText(urlList);
                });
            });
        }
    }

    /**
     * 解析播放页
     * 本地测试: String playHtmlStr = Files.readString(Path.of("playHtml.txt"))
     *
     * @param playHtmlStr 播放页面html字符串
     * @param episode 集数
     * @return 播放对象
     */
    private PlayMovie parsePlayUrl(String playHtmlStr, String episode) {
        PlayMovie playMovie = new PlayMovie();
        playMovie.setEpisode(episode);
        Document html = Jsoup.parse(playHtmlStr);
        Element m3u8UrlEl = html.getElementsByClass("stui-player__video clearfix").getFirst();
        String javaScriptText = m3u8UrlEl.toString();
        String m3u8UrlRegex = "\"url\":\"https:\\\\/\\\\/.*?/index\\.m3u8";
        Pattern m3u8UrlPattern = Pattern.compile(m3u8UrlRegex, Pattern.CASE_INSENSITIVE);
        Matcher m3u8UrlMatcher = m3u8UrlPattern.matcher(javaScriptText);
        if (!m3u8UrlMatcher.find()) {
            return playMovie;
        }
        String m3u8UrlNextRegex = "\"url_next\":\"https:\\\\/\\\\/.*?/index\\.m3u8";
        Pattern m3u8UrlNextPattern = Pattern.compile(m3u8UrlNextRegex, Pattern.CASE_INSENSITIVE);
        Matcher m3u8UrlNextMatcher = m3u8UrlNextPattern.matcher(javaScriptText);
        PlayMovie pm = new PlayMovie(episode, m3u8UrlMatcher.group(0).replace("\\/", "/").replace("\"url\":\"", ""),
            m3u8UrlNextMatcher.find() ? m3u8UrlNextMatcher.group(0).replace("\\/", "/").replace("\"url_next\":\"", "") : "");
        playMovie.setM3u8Url(pm.getM3u8Url());
        playMovie.setNextM3u8Url(pm.getNextM3u8Url());
        return playMovie;
    }
}
