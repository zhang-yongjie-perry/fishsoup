package com.fishsoup.fishweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fishsoup.fishweb.domain.User;
import com.fishsoup.fishweb.exception.BusinessException;
import com.fishsoup.fishweb.mapper.MenuMapper;
import com.fishsoup.fishweb.mapper.UserMapper;
import com.fishsoup.fishweb.service.UserService;
import com.fishsoup.fishweb.util.DateUtils;
import com.fishsoup.fishweb.util.SecurityUtils;
import com.fishsoup.fishweb.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.fishsoup.fishweb.enums.OnlineStatusEnum.OFFLINE;
import static com.fishsoup.fishweb.enums.OnlineStatusEnum.ONLINE;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserMapper userMapper;

    private final MenuMapper menuMapper;

    private final BCryptPasswordEncoder encoder;

    @Override
    public boolean register(User user) throws BusinessException {
        User presentUser = selectOneByUsername(user.getUsername());
        if (!Objects.isNull(presentUser)) {
            throw new BusinessException("用户名已注册");
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = selectOneByUsername(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("用户不存在");
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
    public User selectOneByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        QueryWrapper<User> userQueryWrapper = Wrappers.query();
        userQueryWrapper.eq("username", username);
        return userMapper.selectOne(userQueryWrapper);
    }

    @Override
    public User saveUser(User user) {
        User presentedUser = selectOneByUsername(user.getUsername());
        if (Objects.isNull(presentedUser)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        User toUpdate = new User();
        toUpdate.setId(presentedUser.getId());
        if (user.getSex() != null) {
            toUpdate.setSex(user.getSex());
        }
        if (StringUtils.hasText(user.getPassword())) {
            toUpdate.setPassword(encoder.encode(user.getPassword()));
        }
        if (StringUtils.hasText(user.getMobilePhone())) {
            toUpdate.setMobilePhone(user.getMobilePhone());
        }
        if (StringUtils.hasText(user.getEmail())) {
            toUpdate.setEmail(user.getEmail());
        }
        toUpdate.setUpdateBy(SecurityUtils.getLoginName());
        toUpdate.setUpdateTime(DateUtils.now());
        userMapper.updateById(toUpdate);
        return toUpdate;
    }

    @Override
    public List<User> listUsers() {
        QueryWrapper<User> query = Wrappers.query();
        return userMapper.selectList(query
            .ne("username", SecurityUtils.getLoginName())
            .ne("username", "admin"));
    }
}
