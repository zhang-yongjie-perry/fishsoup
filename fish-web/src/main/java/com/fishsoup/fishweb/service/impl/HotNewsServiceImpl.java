package com.fishsoup.fishweb.service.impl;

import com.fishsoup.entity.news.HotNews;
import com.fishsoup.fishweb.service.HotNewsService;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class HotNewsServiceImpl implements HotNewsService {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<HotNews> listHotNews(HotNews conditions, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Query query = new Query().with(pageable);
        Criteria criteria = new Criteria();
        if (StringUtils.hasText(conditions.getTime())) {
            criteria.and("time").is(conditions.getTime());
        }
        if (StringUtils.hasText(conditions.getTitle())) {
            criteria.and("title").regex(Pattern.compile("^.*" + conditions.getTitle() + ".*$", Pattern.CASE_INSENSITIVE));
        }
        if (StringUtils.hasText(conditions.getSite())) {
            criteria.and("site").is(conditions.getSite());
        }
        if (StringUtils.hasText(conditions.getNewsType())) {
            criteria.and("news_type").is(conditions.getNewsType());
        }
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Order.desc("create_time"), Sort.Order.asc("seq")));
        return mongoTemplate.find(query, HotNews.class);
    }
}
