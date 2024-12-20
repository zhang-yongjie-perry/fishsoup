package com.fishsoup.fishums.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.fishums.domain.User;
import com.fishsoup.fishums.exception.BusinessException;

@SuppressWarnings("all")
public interface UserService {

    boolean register(User user) throws BusinessException;

    boolean logout(User user) throws BusinessException;

    User selectOneByUsername(String username) throws BusinessException;

    User loadUserByUsername(String username) throws BusinessException;

    IPage<User> pageUsers(User user, int pageNum, int pageSize);

    boolean updatePassword(String newPassword);

    boolean updateUser(User user);

    long testArrayList(long num);

    long testLinkedList(long num);
}
