package com.fishsoup.fishdas.controller;

import com.fishsoup.entity.news.HotNews;
import com.fishsoup.fishdas.service.HotNewsService;
import com.fishsoup.fishdas.service.MovieService;
import com.fishsoup.fishdas.service.PictureService;
import com.fishsoup.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/crawl")
public class CrawlNetworkResourceController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    @Qualifier("MovieServiceImpl")
    private MovieService movieServiceImpl;

    @Autowired
    @Qualifier("NunuMovieServiceImpl")
    private MovieService nunuMovieServiceImpl;

    @Autowired
    private PictureService pictureService;

    @Autowired
    @Qualifier("ChinaNewsServiceImpl")
    private HotNewsService chinaNewsServiceImpl;

    @Autowired
    @Qualifier("QqNewsServiceImpl")
    private HotNewsService qqNewsServiceImpl;

    @GetMapping("/hello")
    public String helloDas() {
        System.out.println("hello");
        return "hello DAS";
    }

    @GetMapping("/movie/{title}")
    public boolean crawlMoviesByName(@PathVariable("title") String title) {
        return movieServiceImpl.crawlMoviesByName(title);
    }

    @GetMapping("/episode/{mvId}")
    public boolean crawlEpisodesByMovieId(@PathVariable("mvId") String mvId) {
        return movieServiceImpl.crawlEpisodesByMovieId(mvId);
    }

    @GetMapping("/movie/nunu/{title}")
    public boolean crawlNunuMoviesByName(@PathVariable("title") String title) {
        return nunuMovieServiceImpl.crawlMoviesByName(title);
    }

    @GetMapping("/episode/nunu/{mvId}")
    public boolean crawlNunuEpisodesByMovieId(@PathVariable("mvId") String mvId) {
        return nunuMovieServiceImpl.crawlEpisodesByMovieId(mvId);
    }

    @GetMapping("/m3u8/nunu/{sourceId}")
    public String crawlNunuM3u8Source(@PathVariable("sourceId") String sourceId) {
        return nunuMovieServiceImpl.getM3u8Resource(sourceId);
    }

    @GetMapping("/picture/8k/{pageNum}")
    public boolean crawl8kPic(@PathVariable("pageNum") int pageNum) {
        return pictureService.crawl8kPic(pageNum);
    }

    @GetMapping("/picture/4k/{pageNum}")
    public boolean crawl4kPic(@PathVariable("pageNum") int pageNum) {
        return pictureService.crawl4kPic(pageNum);
    }

    @Scheduled(cron = "0 0 7-18 * * *")
    @GetMapping("/hotNews/chinaNews")
    public boolean crawlChinaNews() {
        return chinaNewsServiceImpl.crawlHotNews();
    }

    @Scheduled(cron = "0 0 7-18 * * *")
    @GetMapping("/hotNews/qqNews")
    public boolean crawlQqNews() {
        return qqNewsServiceImpl.crawlHotNews();
    }

    /**
     * 删除3天前的新闻
     * @return 执行结果
     */
    @Scheduled(cron = "0 0 0 */1 * *")
    @DeleteMapping("/hotNews")
    public boolean removeNews() {
        Query query = new Query(Criteria.where("create_time")
            .lt(DateUtils.addTime(DateUtils.now(), - (Duration.ofDays(3).getSeconds() * 1000))));
        mongoTemplate.remove(query, HotNews.class);
        return true;
    }
}