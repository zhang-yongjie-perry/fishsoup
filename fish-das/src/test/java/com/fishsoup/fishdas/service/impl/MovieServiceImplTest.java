package com.fishsoup.fishdas.service.impl;

import com.fishsoup.fishdas.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MovieServiceImplTest {

    @Autowired
    private MovieService service;

    @Test
    public void crawlMoviesByNameTest() {
        service.crawlMoviesByName("岛");
    }
}
