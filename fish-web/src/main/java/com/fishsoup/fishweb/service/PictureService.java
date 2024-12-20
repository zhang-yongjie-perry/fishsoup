package com.fishsoup.fishweb.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.pic.Picture;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("all")
public interface PictureService {

    AtomicInteger picSearchCount = new AtomicInteger(0);

    List<Picture> listPics8k();

    IPage<Picture> pagePics4k(String title, int pageNum, int pageSize);

    boolean searchPics();
}
