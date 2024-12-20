package com.fishsoup.fishweb.feignService;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("fish-das")
public interface DasFeignService {

    @GetMapping("/das/crawl/hello")
    String helloDas();

    @GetMapping("/das/crawl/movie/{title}")
    boolean crawlMoviesByName(@PathVariable("title") String title);

    @GetMapping("/das/crawl/episode/{mvId}")
    boolean crawlEpisodesByMovieId(@PathVariable("mvId") String mvId);

    @GetMapping("/das/crawl/picture/8k/{pageNum}")
    boolean crawl8kPic(@PathVariable("pageNum") int pageNum);

    @GetMapping("/das/crawl/picture/4k/{pageNum}")
    boolean crawl4kPic(@PathVariable("pageNum") int pageNum);
}
