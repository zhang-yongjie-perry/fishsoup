package com.fishsoup.fishweb.service.impl;

import com.fishsoup.fishweb.exception.BusinessException;
import com.fishsoup.fishweb.service.ImageService;
import com.fishsoup.fishweb.util.JWTUtils;
import com.fishsoup.fishweb.util.StringUtils;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.fishsoup.fishweb.util.StringUtils.PACKAGE_SEPARATOR_CHAR;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final GridFsTemplate gridFsTemplate;

    @Override
    @SuppressWarnings("all")
    public String insertImages(MultipartFile file) throws BusinessException {
        // 获取上传的图片文件后缀名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(PACKAGE_SEPARATOR_CHAR));
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException("图片不可为空");
        }
        // 检查文件名是否合法,避免目录遍历攻击
        String fileName = StringUtils.cleanPath(originalFilename);
        if (fileName.contains("..")) {
            throw new BusinessException("图片名称不合法");
        }
        String imageName = JWTUtils.getUUID().concat(originalFilename);
        String mediaType = MediaType.IMAGE_GIF_VALUE.contains(fileExtension) ? MediaType.IMAGE_GIF_VALUE
            : MediaType.IMAGE_PNG_VALUE.contains(fileExtension) ? MediaType.IMAGE_PNG_VALUE
            : MediaType.IMAGE_JPEG_VALUE;
        try {
            return gridFsTemplate.store(file.getInputStream(), imageName, mediaType).toString();
        } catch (IOException e) {
            throw new BusinessException("图片上传失败, 请联系管理员");
        }
    }

    @Override
    public byte[] getImage(String imageId) throws BusinessException {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(imageId)));
        GridFSFile file = gridFsTemplate.findOne(query);
        GridFsResource gridFsResource = gridFsTemplate.getResource(file);

        if (!gridFsResource.exists()) {
            throw new BusinessException("图片不存在");
        }
        try {
            return gridFsResource.getContentAsByteArray();
        } catch (IOException e) {
            throw new BusinessException("图片下载失败");
        }
    }

    @Override
    public boolean removeImages(List<String> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            return true;
        }
        gridFsTemplate.delete(new Query(Criteria.where("_id").in(
            fileIds.stream().map(fileId -> new ObjectId(fileId)).collect(Collectors.toList())
        )));
        return true;
    }
}
