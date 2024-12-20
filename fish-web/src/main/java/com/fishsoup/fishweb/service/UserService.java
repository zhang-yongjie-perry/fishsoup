package com.fishsoup.fishweb.service;

import com.fishsoup.fishweb.domain.User;
import com.fishsoup.fishweb.exception.BusinessException;

import java.util.List;

@SuppressWarnings("all")
public interface UserService {

    boolean register(User user) throws BusinessException;

    boolean logout(User user);

    User selectOneByUsername(String username);

    User saveUser(User user);

    List<User> listUsers();
}
