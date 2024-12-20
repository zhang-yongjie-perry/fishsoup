package com.fishsoup.fishweb.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishsoup.entity.pic.Picture;
import com.fishsoup.fishweb.feignService.DasFeignService;
import com.fishsoup.fishweb.service.PictureService;
import com.fishsoup.fishweb.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {

    private final MongoTemplate mongoTemplate;

    private final DasFeignService dasFeignService;

    @Override
    public List<Picture> listPics8k() {
        return mongoTemplate
            .find(new Query(Criteria.where("type").is(0).and("status").is(0)).with(Sort.by(Sort.Order.desc("create_time"))),
            Picture.class);
    }

    @Override
    public IPage<Picture> pagePics4k(String title, int pageNum, int pageSize) {
        Query query = new Query();
        Criteria criteria = new Criteria().and("type").is(1).and("status").is(0);
        if (StringUtils.hasText(title)) {
            criteria.and("title").regex(Pattern.compile("^.*" + title + ".*$", Pattern.CASE_INSENSITIVE));
        }
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Order.desc("create_time")));

        // 总数量
        long total = mongoTemplate.count(query, Picture.class);
        IPage<Picture> page = new Page<>(pageNum, pageSize, total);

        // 分页
        query = query.with(PageRequest.of(pageNum - 1, pageSize));
        page.setRecords(mongoTemplate.find(query, Picture.class));
        return page;
    }

    @Override
    public boolean searchPics() {
        picSearchCount.compareAndSet(30, 0);
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            executorService.submit(() -> {
                dasFeignService.crawl8kPic(picSearchCount.get());
            });
            executorService.submit(() -> {
                dasFeignService.crawl4kPic(picSearchCount.getAndIncrement());
            });
        }
        return true;
    }
}
