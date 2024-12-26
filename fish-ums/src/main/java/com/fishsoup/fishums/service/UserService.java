package com.fishsoup.fishums.service;

import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.user.User;

import java.util.List;

@SuppressWarnings("all")
public interface UserService {

    boolean lockAccount(User user) throws BusinessException;

    boolean unlockAccount(List<User> userList) throws BusinessException;
}
