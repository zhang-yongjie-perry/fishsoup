package com.fishsoup.fishuser.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public interface UserService {

    public static final Map<String, String> secretKeyMap = new HashMap<>();

    User login(String username, String password) throws BusinessException;

    boolean register(User user) throws BusinessException;

    boolean logout(User user);

    User selectOneByUsername(String username);

    User saveUser(User user);

    boolean batchSaveUser(List<User> userList);

    List<User> listUsers();

    List<User> listChatUsers();

    boolean updatePassword(User user) throws BusinessException;

    IPage<User> pageUsers(User user, int pageNum, int pageSize);
}
