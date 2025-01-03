package com.fishsoup.fishweb.service.impl;

import com.fishsoup.fishweb.domain.Memo;
import com.fishsoup.fishweb.service.MemoService;
import com.fishsoup.fishweb.util.UserUtils;
import com.fishsoup.util.DateUtils;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemoServiceImpl implements MemoService {

    private final MongoTemplate mongoTemplate;

    @Override
    public boolean saveMemo(Memo memo) {
        String today = DateUtils.now(DateUtils.YYYY_MM_DD);
        String loginName = UserUtils.getLoginName();

        Update update = new Update();
        update.set("username", loginName);
        update.set("content", memo.getContent());
        update.set("updateTime", DateUtils.now());

        Query query = new Query(Criteria.where("username").is(loginName));
        if (StringUtils.hasText(memo.getDate())) {
            query.addCriteria(Criteria.where("date").is(memo.getDate()));
            update.set("date", memo.getDate());
        } else {
            query.addCriteria(Criteria.where("date").is(today));
            update.set("date", today);
        }
        mongoTemplate.upsert(query, update, Memo.class);
        return true;
    }

    @Override
    public Memo getMemo(String date) {
        date = StringUtils.hasText(date) ? date.trim() : DateUtils.now(DateUtils.YYYY_MM_DD);
        String loginName = UserUtils.getLoginName();
        return mongoTemplate.findOne(Query.query(Criteria.where("username").is(loginName).and("date").is(date)), Memo.class);
    }
}
