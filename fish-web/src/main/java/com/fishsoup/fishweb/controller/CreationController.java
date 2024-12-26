package com.fishsoup.fishweb.controller;

import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.fishweb.domain.Creation;
import com.fishsoup.fishweb.service.CreationService;
import com.fishsoup.fishweb.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/creation")
public class CreationController {

    private final CreationService creationService;

    private final ImageService imageService;

    @PostMapping()
    public ResponseResult saveCreation(@RequestBody Creation creation) throws BusinessException {
        // 删除图片
        imageService.removeImages(creation.getToDelImages());
        // 保存文档内容
        return ResponseResult.success("保存成功", creationService.saveCreation(creation));
    }

    @GetMapping("/{id}")
    public ResponseResult getCreationById(@PathVariable("id") String id) {
        Creation creation = creationService.getCreationById(id);
        return ResponseResult.success(creation);
    }

    @PostMapping("/list/{pageNum}/{pageSize}")
    public List<Creation> listCreations(@RequestBody Creation creation, @PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize) {
        return creationService.listCreations(creation, pageNum, pageSize);
    }
}
