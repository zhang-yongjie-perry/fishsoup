package com.fishsoup.fishweb.service;

import com.fishsoup.fishweb.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    String insertImages(MultipartFile file) throws BusinessException;

    byte[] getImage(String imageName) throws BusinessException;

    boolean removeImages(List<String> imageNames);
}
