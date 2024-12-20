package com.fishsoup.fishdas.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PictureServiceImplTest {

    @Autowired
    PictureServiceImpl pictureService;

    @Test
    public void test() {
        pictureService.crawl8kPic(1);
    }

    @Test
    public void test2() {
        String a = String.format("输出格式化字符串：%s", "");
        pictureService.crawl4kPic(1);
        System.out.println(a);
    }
}
