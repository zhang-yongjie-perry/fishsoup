package com.fishsoup.fishweb.feignService;

import com.fishsoup.entity.http.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("fish-das")
public interface DasFeignService {

    @GetMapping("/das/crawl/movie/{title}")
    ResponseResult crawlMoviesByName(@PathVariable("title") String title);

    @GetMapping("/das/crawl/movie/nunu/{title}")
    ResponseResult crawlNunuMoviesByName(@PathVariable("title") String title);

    @GetMapping("/das/crawl/m3u8/nunu/{sourceId}")
    ResponseResult crawlNunuM3u8Source(@PathVariable("sourceId") String sourceId);

    @GetMapping("/das/crawl/episode/{mvId}")
    ResponseResult crawlEpisodesByMovieId(@PathVariable("mvId") String mvId);

    @GetMapping("/das/crawl/picture/8k/{pageNum}")
    ResponseResult crawl8kPic(@PathVariable("pageNum") int pageNum);

    @GetMapping("/das/crawl/picture/4k/{pageNum}")
    ResponseResult crawl4kPic(@PathVariable("pageNum") int pageNum);
}
