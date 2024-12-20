package com.fishsoup.fishchat.service;

import com.fishsoup.fishchat.domain.User;

import java.util.List;

@SuppressWarnings("all")
public interface UserService {
    List<User> listUsers();
}
