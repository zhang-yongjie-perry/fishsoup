package com.fishsoup.fishweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.pic.Picture;
import com.fishsoup.fishweb.http.ResponseResult;
import com.fishsoup.fishweb.service.PictureService;
import com.fishsoup.fishweb.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/picture")
public class PictureController {

    private final PictureService pictureService;

    @GetMapping("/8k")
    public List<Picture> listPics8k() {
        return pictureService.listPics8k();
    }

    @PostMapping("/4k/{pageNum}/{pageSize}")
    public IPage<Picture> listPics4kPage(@RequestBody(required = false) Picture picture,
                                         @PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize) {
        return pictureService.pagePics4k(Objects.isNull(picture) ? StringUtils.EMPTY : picture.getTitle(), pageNum, pageSize);
    }

    @GetMapping("/search")
    public ResponseResult searchPics() {
        pictureService.searchPics();
        return ResponseResult.success();
    }
}
