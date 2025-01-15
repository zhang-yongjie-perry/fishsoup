package com.fishsoup.fishweb.service;

import com.fishsoup.entity.creation.Creation;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.enums.CreationClassifyEnum;

import java.util.List;

public interface CreationService {

    String saveCreation(Creation creation) throws BusinessException;

    Creation getCreationById(String id);

    List<Creation> listCreations(Creation conditions, int pageNum, int pageSize);

    void deleteCreationsCache(CreationClassifyEnum classify);
}
