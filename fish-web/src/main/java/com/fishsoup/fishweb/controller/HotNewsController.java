package com.fishsoup.fishweb.controller;

import com.fishsoup.entity.news.HotNews;
import com.fishsoup.fishweb.service.HotNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotNews")
public class HotNewsController {

    private final HotNewsService hotNewsService;

    @PostMapping("/list/{page}/{limit}")
    public List<HotNews> listHotNews(@RequestBody HotNews conditions, @PathVariable("page") int page,
                                     @PathVariable("limit") int limit) {
        return hotNewsService.listHotNews(conditions, page - 1, limit);
    }
}
