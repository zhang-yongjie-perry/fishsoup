package com.fishsoup.fishchat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fishsoup.fishchat.domain.User;
import com.fishsoup.fishchat.mapper.UserMapper;
import com.fishsoup.fishchat.service.UserService;
import com.fishsoup.fishchat.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public List<User> listUsers() {
        QueryWrapper<User> query = Wrappers.query();
        return userMapper.selectList(query
            .ne("username", SecurityUtils.getLoginName())
            .ne("username", "admin"));
    }
}
