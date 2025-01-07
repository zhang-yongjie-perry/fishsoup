package com.fishsoup.fishdas.controller;

import com.fishsoup.annotation.DistributedLock;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.entity.news.HotNews;
import com.fishsoup.fishdas.service.HotNewsService;
import com.fishsoup.fishdas.service.MovieService;
import com.fishsoup.fishdas.service.PictureService;
import com.fishsoup.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
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

    @Autowired
    @Qualifier("WeiboNewsServiceImpl")
    private HotNewsService weiboNewsServiceImpl;

    @GetMapping("/movie/{title}")
    @DistributedLock(name = "crawlMoviesByName", leaseTime = 60 * 30)
    public ResponseResult crawlMoviesByName(@PathVariable("title") String title) {
        movieServiceImpl.crawlMoviesByName(title);
        return ResponseResult.success();
    }

    @GetMapping("/episode/{mvId}")
    @DistributedLock(name = "crawlEpisodesByMovieId", leaseTime = 60 * 30)
    public ResponseResult crawlEpisodesByMovieId(@PathVariable("mvId") String mvId) {
        movieServiceImpl.crawlEpisodesByMovieId(mvId);
        return ResponseResult.success();
    }

    @GetMapping("/movie/nunu/{title}")
    @DistributedLock(name = "crawlNunuMoviesByName", leaseTime = 60 * 30)
    public ResponseResult crawlNunuMoviesByName(@PathVariable("title") String title) {
        nunuMovieServiceImpl.crawlMoviesByName(title);
        return ResponseResult.success();
    }

    @GetMapping("/m3u8/nunu/{sourceId}")
    public ResponseResult crawlNunuM3u8Source(@PathVariable("sourceId") String sourceId) {
        String m3u8Resource = nunuMovieServiceImpl.getM3u8Resource(sourceId);
        return ResponseResult.success(m3u8Resource);
    }

    @GetMapping("/picture/8k/{pageNum}")
    @DistributedLock(name = "crawl8kPic", leaseTime = 60 * 20)
    public ResponseResult crawl8kPic(@PathVariable("pageNum") int pageNum) {
        pictureService.crawl8kPic(pageNum);
        return ResponseResult.success();
    }

    @GetMapping("/picture/4k/{pageNum}")
    @DistributedLock(name = "crawl4kPic", leaseTime = 60 * 20)
    public ResponseResult crawl4kPic(@PathVariable("pageNum") int pageNum) {
        pictureService.crawl4kPic(pageNum);
        return ResponseResult.success();
    }

    @Scheduled(cron = "0 0 7-22 * * *")
    @DistributedLock(name = "crawlChinaNews")
    @GetMapping("/hotNews/chinaNews")
    public boolean crawlChinaNews() {
        return chinaNewsServiceImpl.crawlHotNews();
    }

    @Scheduled(cron = "0 0 7-22 * * *")
    @DistributedLock(name = "qqNews")
    @GetMapping("/hotNews/qqNews")
    public boolean crawlQqNews() {
        return qqNewsServiceImpl.crawlHotNews();
    }

    @Scheduled(cron = "0 0/30 7-22 * * *")
    @DistributedLock(name = "weiboNews")
    @GetMapping("/hotNews/weiboNews")
    public boolean crawlWeiboNews() {
        return weiboNewsServiceImpl.crawlHotNews();
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