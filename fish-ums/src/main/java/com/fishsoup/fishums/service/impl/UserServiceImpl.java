package com.fishsoup.fishums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishsoup.fishums.domain.User;
import com.fishsoup.fishums.enums.SexEnum;
import com.fishsoup.fishums.enums.UserTypeEnum;
import com.fishsoup.fishums.mapper.MenuMapper;
import com.fishsoup.fishums.mapper.UserMapper;
import com.fishsoup.fishums.service.UserService;
import com.fishsoup.utils.DateUtils;
import com.fishsoup.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.fishsoup.fishums.enums.OnlineStatusEnum.OFFLINE;
import static com.fishsoup.fishums.enums.OnlineStatusEnum.ONLINE;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final MenuMapper menuMapper;

    private final BCryptPasswordEncoder encoder;

    @Override
    public boolean register(User user) {
        User presentUser = selectOneByUsername(user.getUsername());
        if (!Objects.isNull(presentUser)) {
            throw new AuthenticationException("用户名已注册");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreateBy(user.getUsername());
        user.setCreateTime(DateUtils.now());
        user.setUpdateBy(user.getCreateBy());
        user.setUpdateTime(user.getCreateTime());
        return userMapper.insert(user) == 1;
    }

    @Override
    public boolean logout(User user) {
        User presentUser = selectOneByUsername(user.getUsername());
        if (Objects.isNull(presentUser)) {
            return true;
        }
        User toUpdate = new User();
        toUpdate.setId(presentUser.getId());
        toUpdate.setOnlineStatus(OFFLINE);
        return userMapper.updateById(toUpdate) == 1;
    }

    public User loadUserByUsername(String username) {
        User user = selectOneByUsername(username);
        if (Objects.isNull(user)) {
            throw new AuthenticationException("用户不存在");
        }
        if (!Objects.equals(UserTypeEnum.ADMIN, user.getType())) {
            throw new AuthenticationException("非管理员账号, 无权登录");
        }
        User toUpdate = new User();
        toUpdate.setId(user.getId());
        toUpdate.setOnlineStatus(ONLINE);
        toUpdate.setLastLoginTime(DateUtils.formatDate(DateUtils.now(), DateUtils.YYYY_MM_DD_HH_MI_SS));
        userMapper.updateById(toUpdate);
        user.setPerms(menuMapper.listPermsByUserId(user.getId()));
        return user;
    }

    @Override
    public IPage<User> pageUsers(User user, int pageNum, int pageSize) {
        IPage<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (user != null && user.getUsername() != null) {
            queryWrapper.like("username", user.getUsername());
        }
        page.setTotal(userMapper.selectCount(queryWrapper));
        queryWrapper.orderByDesc("update_time");
        return userMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean updatePassword(String newPassword) {
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        User toUpdate = new User();
        toUpdate.setId(currentUser.getId());
        toUpdate.setPassword(encoder.encode(newPassword));
        toUpdate.setUpdateBy(currentUser.getUsername());
        toUpdate.setUpdateTime(DateUtils.now());
        userMapper.updateById(toUpdate);
        return true;
    }

    @Override
    public boolean updateUser(User user) {
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
        User toUpdate = new User();
        toUpdate.setId(currentUser.getId());
        toUpdate.setMobilePhone(user.getMobilePhone());
        toUpdate.setEmail(user.getEmail());
        toUpdate.setSex(user.getSex());
        toUpdate.setUpdateBy(currentUser.getUsername());
        toUpdate.setUpdateTime(DateUtils.now());
        userMapper.updateById(toUpdate);
        return false;
    }

    @Override
    public long testArrayList(long num) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> list = new ArrayList<>();
        LongStream.range(0, num).forEach(index -> {
            var user = new User().setId(String.valueOf(index))
                .setSex(SexEnum.MALE).setMobilePhone(String.valueOf(Math.random() * 11))
                .setPassword(encoder.encode("123456"))
                .setOnlineStatus(ONLINE)
                .setEmail("xxxxxxxxxxx@qq.com")
                .setCreateTime(DateUtils.now()).setUpdateTime(DateUtils.now());
            list.add(user);
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        stopWatch.stop();
        return stopWatch.getTotalTimeMillis();
    }

    @Override
    public long testLinkedList(long num) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> list = new LinkedList<>();
        LongStream.range(0, num).forEach(index -> {
            var user = new User().setId(String.valueOf(index))
                .setSex(SexEnum.FEMALE).setMobilePhone(String.valueOf(Math.random() * 11))
                .setPassword(encoder.encode("654321"))
                .setOnlineStatus(ONLINE)
                .setEmail("xxxxxxxxxxx@qq.com")
                .setCreateTime(DateUtils.now()).setUpdateTime(DateUtils.now());
            list.add(user);
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        stopWatch.stop();
        return stopWatch.getTotalTimeMillis();
    }


    @Override
    public User selectOneByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new AuthenticationException("用户名不能为空");
        }
        QueryWrapper<User> userQueryWrapper = Wrappers.query();
        userQueryWrapper.eq("username", username);
        return userMapper.selectOne(userQueryWrapper);
    }
}
