package com.fishsoup.fishdas.service.impl;

import com.fishsoup.entity.pic.Picture;
import com.fishsoup.fishdas.network.OkHttpUtils;
import com.fishsoup.fishdas.service.PictureService;
import com.fishsoup.util.DateUtils;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {

    private final OkHttpUtils okHttpUtils;

    private final GridFsTemplate gridFsTemplate;

    private final MongoTemplate mongoTemplate;

    @Override
    public boolean crawl8kPic(int pageNum) {
        String response = okHttpUtils.get(pageNum == 0 ? SEARCH_PAGE_8K : String.format(SEARCH_PAGE_8K.concat("%d"), pageNum), HEADERS, null);
        if (!StringUtils.hasText(response)) {
            return false;
        }
        Document htmlDoc = Jsoup.parse(response);
        Elements pics8k = htmlDoc.getElementsByClass("item-img");
        List<Picture> pic8ks = mongoTemplate.find(new Query(Criteria.where("type").is(0)), Picture.class);
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            pic8ks.forEach(p -> {
                executorService.submit(() -> {
                    p.setStatus(1);
                    mongoTemplate.save(p);
                });
            });
        }
        IntStream.range(0, 8).forEach(i -> {
            Thread.startVirtualThread(() -> {
                Element picParent = pics8k.get(i);
                Element imgEl = picParent.getElementsByTag("img").getFirst();
                String picTitle = imgEl.attr("alt");
                Query query = new Query(Criteria.where("title").is(picTitle));
                Picture one = mongoTemplate.findOne(query, Picture.class);
                if (one != null) {
                    one.setStatus(0);
                    mongoTemplate.save(one);
                    return;
                }
                ObjectId thumbsId = new ObjectId();
                ObjectId fileId;
                // 保存缩略图
                try (Response res = okHttpUtils.downloadImage(imgEl.attr("src"), HEADERS, null)) {
                    if (res.isSuccessful() && res.body() != null) {
                        thumbsId = gridFsTemplate.store(res.body().byteStream(), picTitle.concat(".jpg-pcthumbs"), MediaType.IMAGE_JPEG_VALUE);
                    }
                }
                // 保存原图片
                try (Response res = okHttpUtils.downloadImage(imgEl.attr("src").replace(RUL_SUFFIX, ""), HEADERS, null)) {
                    if (!res.isSuccessful()) {
                        return;
                    }
                    if (res.body() == null) {
                        return;
                    }
                    fileId = gridFsTemplate.store(res.body().byteStream(), picTitle.concat(".jpg"), MediaType.IMAGE_JPEG_VALUE);
                }
                Picture pic = new Picture().setStatus(0).setType(0).setTitle(picTitle).setCreateTime(DateUtils.now())
                    .setThumbId(thumbsId.toString()).setFileId(fileId.toString());
                mongoTemplate.insert(pic);
            });
        });
        return true;
    }

    @Override
    public boolean crawl4kPic(int pageNum) {
        String response = okHttpUtils.get(pageNum == 0 ? SEARCH_PAGE_4k : String.format(SEARCH_PAGE_4k.concat("%d"), pageNum), HEADERS, null);
        Document htmlDoc = Jsoup.parse(response);
        Elements pics4k = htmlDoc.getElementsByClass("item-img");
        pics4k.forEach(picParent -> {
            Thread.startVirtualThread(() -> {
                Element imgEl = picParent.getElementsByTag("img").getFirst();
                String picTitle = imgEl.attr("alt");
                Query query = new Query(Criteria.where("title").is(picTitle));
                Picture one = mongoTemplate.findOne(query, Picture.class);
                if (one != null) {
                    return;
                }
                ObjectId thumbsId = new ObjectId();
                ObjectId fileId;
                // 保存缩略图
                try (Response res = okHttpUtils.downloadImage(imgEl.attr("src"), HEADERS, null)) {
                    if (res.isSuccessful() && res.body() != null) {
                        thumbsId = gridFsTemplate.store(res.body().byteStream(), picTitle.concat(".jpg-pcthumbs"), MediaType.IMAGE_JPEG_VALUE);
                    }
                }
                // 保存原图片
                try (Response res = okHttpUtils.downloadImage(imgEl.attr("src").replace(RUL_SUFFIX, ""), HEADERS, null)) {
                    if (!res.isSuccessful()) {
                        return;
                    }
                    if (res.body() == null) {
                        return;
                    }
                    fileId = gridFsTemplate.store(res.body().byteStream(), picTitle.concat(".jpg"), MediaType.IMAGE_JPEG_VALUE);
                }
                Picture pic = new Picture().setStatus(0).setType(1).setTitle(picTitle).setCreateTime(DateUtils.now())
                    .setThumbId(thumbsId.toString()).setFileId(fileId.toString());
                mongoTemplate.insert(pic);
            });
        });
        return true;
    }
}
