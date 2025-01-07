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

import java.time.Duration;
import java.util.*;

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
        update.set("content", StringUtils.hasText(memo.getContent()) ? memo.getContent() : "");
        update.set("updateTime", DateUtils.now());
        update.set("color", StringUtils.hasText(memo.getColor()) ? memo.getColor() : "transparent");

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
        return mongoTemplate.findOne(Query.query(Criteria.where("username").is(loginName)
            .and("date").is(date)), Memo.class);
    }

    @Override
    public Map<String, Object> getMemoList(String dateStr) {
        Date date = DateUtils.parse(dateStr, DateUtils.YYYY_MM_DD);
        Date past = DateUtils.addTime(date, -(Duration.ofDays(45).getSeconds() * 1000));
        Date future = DateUtils.addTime(date, Duration.ofDays(45).getSeconds() * 1000);
        Query query = new Query(Criteria.where("date")
            .gte(DateUtils.formatDate(past, DateUtils.YYYY_MM_DD))
            .lte(DateUtils.formatDate(future, DateUtils.YYYY_MM_DD))
            .and("username").is(UserUtils.getLoginName()));
        List<Memo> memos = mongoTemplate.find(query, Memo.class);
        Map<String, Object> map = new HashMap<>();
        memos.forEach(memo -> {
            List<Map<String, String>> todoList = new ArrayList<>();
            String[] split = StringUtils.hasText(memo.getContent()) ? memo.getContent().split("\n")
                : new String[0];
            for (String content : split) {
                Map<String, String> todoMap = new HashMap<>();
                if (content.startsWith("#")) {
                    content = content.substring(1);
                    todoMap.put("type", "warning");
                } else if (content.startsWith("$")) {
                    content = content.substring(1);
                    todoMap.put("type", "success");
                } else {
                    if (todoList.isEmpty()) {
                        todoMap.put("type", "warning");
                        todoMap.put("content", content);
                        todoList.add(todoMap);
                        continue;
                    }
                    Map<String, String> last = todoList.getLast();
                    last.put("content", last.get("content") + "\n" + content);
                    continue;
                }
                todoMap.put("content", content);
                todoList.add(todoMap);
            }
            Map<String, Object> todoMap = new HashMap<>();
            todoMap.put("todo", todoList);
            todoMap.put("color", StringUtils.hasText(memo.getColor()) ? memo.getColor() : "transparent");
            map.put(memo.getDate(), todoMap);
        });
        return map;
    }


}
