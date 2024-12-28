package com.fishsoup.fishdas.controller;

import com.fishsoup.fishdas.service.MovieService;
import com.fishsoup.fishdas.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crawl")
public class CrawlNetworkResourceController {

    @Autowired
    @Qualifier("MovieServiceImpl")
    private MovieService movieServiceImpl;

    @Autowired
    @Qualifier("NunuMovieServiceImpl")
    private MovieService nunuMovieServiceImpl;

    @Autowired
    private PictureService pictureService;

    @GetMapping("/hello")
    public String helloDas() {
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
}