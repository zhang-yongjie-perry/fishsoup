package com.fishsoup.fishdas.controller;

import com.fishsoup.fishdas.service.MovieService;
import com.fishsoup.fishdas.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crawl")
public class CrawlNetworkResourceController {

    private final MovieService movieService;

    private final PictureService pictureService;

    @GetMapping("/hello")
    public String helloDas() {
        return "hello DAS";
    }

    @GetMapping("/movie/{title}")
    public boolean crawlMoviesByName(@PathVariable("title") String title) {
        return movieService.crawlMoviesByName(title);
    }

    @GetMapping("/episode/{mvId}")
    public boolean crawlEpisodesByMovieId(@PathVariable("mvId") String mvId) {
        return movieService.crawlEpisodesByMovieId(mvId);
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