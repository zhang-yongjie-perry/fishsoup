package com.fishsoup.fishweb.controller;

import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.fishweb.http.UploadResult;
import com.fishsoup.fishweb.service.ImageService;
import com.fishsoup.util.StringUtils;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final GridFsTemplate gridFsTemplate;

    private final ImageService imageService;

    @PostMapping("/upload")
    public UploadResult uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileId = imageService.insertImages(file);
            return UploadResult.success(fileId, fileId, StringUtils.EMPTY);
        } catch (BusinessException e) {
            return UploadResult.error(e.getMessage());
        }
    }

    @GetMapping("/download/{imageId}")
    public ResponseEntity downloadImage(@PathVariable("imageId") String imageId) throws BusinessException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity(imageService.getImage(imageId), headers, HttpStatus.OK);
    }

    @Deprecated
    @GetMapping("/download/response")
    public void downloadImageWithResponse(String imageName, HttpServletResponse response) throws IOException {
        Query query = new Query(Criteria.where("filename").is(imageName));
        GridFSFile file = gridFsTemplate.findOne(query);
        GridFsResource gridFsResource = gridFsTemplate.getResource(file);

        if (gridFsResource == null || !gridFsResource.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // 设置响应的内容类型
        String contentType = file.getMetadata().get("_contentType").toString();
        response.setContentType(contentType);

        // 获取文件的输入流并写入响应的输出流
        try (InputStream inputStream = gridFsResource.getInputStream();
            ServletOutputStream outputStream = response.getOutputStream()) {
            // 使用Apache Commons IO库来复制流
//            IOUtils.copy(inputStream, outputStream);
            response.flushBuffer();
        }
    }
}
