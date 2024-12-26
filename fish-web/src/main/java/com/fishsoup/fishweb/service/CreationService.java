package com.fishsoup.fishweb.service;

import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.fishweb.domain.Creation;

import java.util.List;

public interface CreationService {

    String saveCreation(Creation creation) throws BusinessException;

    Creation getCreationById(String id);

    List<Creation> listCreations(Creation conditions, int pageNum, int pageSize);
}
