package com.fishsoup.fishdas.service.impl;

import com.fishsoup.fishdas.service.NovelService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class NovelServiceImplTest {

    @Autowired
    private NovelService novelService;

    @Test
    public void crawlNovelTest() {
        novelService.crawlNovelsByName("小李飞刀");
    }
}
