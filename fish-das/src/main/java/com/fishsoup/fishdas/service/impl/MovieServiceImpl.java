package com.fishsoup.fishdas.service.impl;

import com.fishsoup.entity.movie.PlayMovie;
import com.fishsoup.entity.movie.PlayOrg;
import com.fishsoup.entity.movie.TvMovie;
import com.fishsoup.enums.MovieStatusEnum;
import com.fishsoup.fishdas.service.MovieService;
import com.fishsoup.utils.CollectionUtils;
import com.fishsoup.utils.DateUtils;
import com.fishsoup.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final static String REFERER_URL = "https://www.fangsendq.com/";
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
    private final static String SEARCH_PAGE_REGEX = "-{10}\\d+-{3}";
    private final static Headers HEADERS = new Headers.Builder().add("referer", REFERER_URL).add("user-agent", USER_AGENT).build();

    private final OkHttpClient okHttpClient;

    private final MongoTemplate mongoTemplate;

    @Override
    public boolean crawlMoviesByName(String title) {
        String requestUrl = String.format("https://www.fangsendq.com/vodsearch/-------------.html?wd=%s&submit=", title);
        // 1. 解析搜索结果列表
        List<TvMovie> mvs = Collections.synchronizedList(new ArrayList<>());
        toParseSearchResultHtml(requestUrl, mvs);
        // 2. 解析播放首页页
        if (CollectionUtils.isEmpty(mvs)) {
            return false;
        }
        /*
            受限于目标网站并发限制, 此处暂时不对剧集播放首页及剧集的m3u8地址进行获取, 当页面点击播放按钮时再进行获取
            toParsePlayHomeUrl(mvs);
            topParsePlayUrl(mvs);
         */
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

    private void saveMv(TvMovie mv) {
        Query query = new Query(Criteria.where("title").is(mv.getTitle()));
        Update update = new Update();
        update.set("sort_num", mv.getSortNum());
        update.set("img_url", mv.getImgUrl());
        update.set("synopsis", mv.getSynopsis());
        update.set("play_home_url", mv.getPlayHomeUrl());
        update.set("status", mv.getStatus());
        update.set("last_update_time", mv.getLastUpdateTime());
        update.set("play_orgs", mv.getPlayOrgs());
        mongoTemplate.upsert(query, update, TvMovie.class);
    }

    @Override
    public boolean crawlEpisodesByMovieId(String id) {
        TvMovie mv = mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), TvMovie.class);
        if (mv == null) {
            return false;
        }
        List<TvMovie> mvs = List.of(mv);
        toParsePlayHomeUrl(mvs);
        topParsePlayUrl(mvs);
        saveMv(mv);
        return true;
    }

    /**
     * 解析播放地址
     * @param mvs 剧集列表
     */
    private void topParsePlayUrl(List<TvMovie> mvs) {
        mvs.parallelStream().forEach(mv -> {
            if (CollectionUtils.isEmpty(mv.getPlayOrgs())) {
                return;
            }
            mv.getPlayOrgs().parallelStream().forEach(playOrg -> {
                Elements episodeEls = playOrg.getEpisodeEls();
                if (episodeEls.size() > 100) {
                    return;
                }
                List<PlayMovie> playList = Collections.synchronizedList(new ArrayList<>());
                try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
                    episodeEls.forEach(episodeEl -> {
                        executorService.submit(() -> {
                            // 请求播放页以获取m3u8地址
                            String playPageHtmlStr = request(episodeEl.attr("href"));
                            if (!StringUtils.hasText(playPageHtmlStr)) {
                                playList.add(new PlayMovie().setEpisode(episodeEl.text()));
                                return;
                            }
                            PlayMovie playMovie = parsePlayUrl(playPageHtmlStr, episodeEl.text());
                            playList.add(playMovie);
                            log.info("剧名: {}, 剧集: {}, m3u8地址: {}", mv.getTitle(), playMovie.getEpisode(), playMovie.getM3u8Url());
                        });
                    });
                }
                playOrg.setLastEpisode(playList.getLast().getEpisode()).setPlayList(playList.stream().sorted().toList());
            });
            mv.setStatus(MovieStatusEnum.COMPLETED.getCode());
        });
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
        Elements playListEls = html.getElementsByClass("stui-vodlist__media col-pd clearfix").getFirst().getElementsByTag("li");
        List<TvMovie> resultMvs = playListEls.parallelStream().map(element -> {
            Elements aEls = element.getElementsByTag("a");
            String imgUrl = aEls.getFirst().attr("data-original");
            String title = aEls.get(1).text();
            String detailsUrl = aEls.get(3).attr("href");
            return new TvMovie().setTitle(title).setStatus(MovieStatusEnum.INIT.getCode()).setSortNum(0).setImgUrl(imgUrl).setPlayHomeUrl(detailsUrl).setLastUpdateTime(DateUtils.now());
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
        Elements pageListEls = html.getElementsByClass("stui-page text-center clearfix");
        if (pageListEls.isEmpty()) {
            return;
        }
        Elements aTagEls = pageListEls.getFirst().getElementsByTag("a");
        int size = aTagEls.size();
        if (size < 2) {
            return;
        }
        String lastUrl = aTagEls.get(size - 1).attr("href");
        Matcher matcher = Pattern.compile(SEARCH_PAGE_REGEX).matcher(lastUrl);
        if (!matcher.find()) {
            return;
        }
        int lastPageNum = Integer.parseInt(matcher.group(0).replaceAll("-+", ""));
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(1, lastPageNum + 1).forEach(i -> executorService.submit(() -> {
                doParseSearchResultHtml(matcher.replaceAll(String.format("----------%d---", i)), mvs);
            }));
        }
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
                    String playHomeHtmlStr = request(mv.getPlayHomeUrl());
                    Document html = Jsoup.parse(playHomeHtmlStr);
                    String synopsis = html.getElementsByClass("stui-vodlist__thumb picture v-thumb")
                        .getFirst().getElementsByTag("span").getLast().text();
                    mv.setSynopsis(synopsis);
                    mv.setLastUpdateTime(DateUtils.now());
                    Element playEl = html.getElementsByClass("stui-pannel-box b playlist mb").getFirst();
                    Element playOrgEl = playEl.getElementsByClass("nav nav-tabs active").getFirst();
                    Elements playOrgTags = playOrgEl.getElementsByTag("a");
                    List<PlayOrg> playOrgs = playOrgTags.stream().map(element -> {
                        PlayOrg playOrg = new PlayOrg().setOrgName(element.text());
                        String playUrlId = element.attr("href").substring(1);
                        Elements episodeEls = Objects.requireNonNull(playEl.getElementById(playUrlId)).getElementsByTag("a");
                        playOrg.setEpisodeEls(episodeEls);
                        return playOrg;
                    }).toList();
                    mv.setPlayOrgs(playOrgs);
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
