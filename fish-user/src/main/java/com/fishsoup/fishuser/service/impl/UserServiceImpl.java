package com.fishsoup.fishuser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.user.User;
import com.fishsoup.enums.AccountStatusEnum;
import com.fishsoup.enums.YesNoEnum;
import com.fishsoup.fishuser.mapper.MenuMapper;
import com.fishsoup.fishuser.mapper.UserMapper;
import com.fishsoup.fishuser.service.UserService;
import com.fishsoup.fishuser.util.UserUtils;
import com.fishsoup.util.DateUtils;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.fishsoup.enums.OnlineStatusEnum.OFFLINE;
import static com.fishsoup.enums.OnlineStatusEnum.ONLINE;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final MenuMapper menuMapper;

    private final BCryptPasswordEncoder encoder;

    private final RedissonClient redisson;

    @Override
    public User login(String username, String password) throws BusinessException {
        User user = selectOneByUsername(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("用户不存在");
        }

        if (Objects.equals(user.getAccountStatus(), AccountStatusEnum.EXPIRED)) {
            throw new BusinessException("账号已失效, 请联系管理员");
        }

        if (Objects.equals(user.getAccountStatus(), AccountStatusEnum.LOCKED)) {
            throw new BusinessException("账号已锁定, 请联系管理员");
        }

        if (!encoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        user.setPassword(null);

        User toUpdate = new User();
        toUpdate.setId(user.getId());
        toUpdate.setOnlineStatus(ONLINE);
        toUpdate.setLastLoginTime(DateUtils.formatDate(DateUtils.now(), DateUtils.YYYY_MM_DD_HH_MI_SS));
        userMapper.updateById(toUpdate);
        user.setOnlineStatus(ONLINE);
        user.setLastLoginTime(toUpdate.getLastLoginTime());
        user.setPerms(menuMapper.listPermsByUserId(user.getId()));
        // 用户信息保存到redis
        RBucket<User> bucket = redisson.getBucket("fish:user:" + user.getUsername());
        bucket.set(user);
        // 设置登录过期时间2小时
        bucket.expire(Duration.ofHours(24));
        return user;
    }

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
        // 删除redis的用户缓存
        RBucket<User> bucket = redisson.getBucket("fish:user:" + presentUser.getUsername());
        User cacheUser = bucket.get();
        if (!Objects.isNull(cacheUser)) {
            boolean delete = bucket.delete();
            if (!delete) {
                throw new BusinessException("账号退出登录失败: 缓存删除失败");
            }
        }
        User toUpdate = new User();
        toUpdate.setId(presentUser.getId());
        toUpdate.setOnlineStatus(OFFLINE);
        return userMapper.updateById(toUpdate) == 1;
    }

    @Override
    public User selectOneByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        QueryWrapper<User> userQueryWrapper = Wrappers.query();
        userQueryWrapper.eq("username", username).eq("del_flag", YesNoEnum.NO.getCode());
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
        if (user.getAccountStatus() != null) {
            toUpdate.setAccountStatus(user.getAccountStatus());
        }
        toUpdate.setUpdateBy(UserUtils.getLoginName());
        toUpdate.setUpdateTime(DateUtils.now());
        userMapper.updateById(toUpdate);
        toUpdate.setPassword(null);
        return toUpdate;
    }

    @Override
    public boolean batchSaveUser(List<User> userList) {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            userList.forEach(user -> {
                executorService.submit(() -> saveUser(user));
            });
        }
        return true;
    }

    @Override
    public List<User> listUsers() {
        QueryWrapper<User> query = Wrappers.query();
        query.select(User.columns).eq("del_flag", YesNoEnum.NO.getCode());
        return userMapper.selectList(query);
    }

    @Override
    public List<User> listChatUsers() {
        QueryWrapper<User> query = Wrappers.query();
        return userMapper.selectList(query
            .select(User.columns)
            .eq("del_flag", YesNoEnum.NO.getCode())
            .ne("username", UserUtils.getLoginName())
            .ne("username", "admin"));
    }

    @Override
    public boolean updatePassword(User user) throws BusinessException {
        User presentedUser = selectOneByUsername(user.getUsername());
        if (Objects.isNull(presentedUser)) {
            return false;
        }
        if (!encoder.matches(user.getPassword(), presentedUser.getPassword())) {
            throw new BusinessException("旧密码错误, 请重试");
        }
        User toUpdate = new User();
        toUpdate.setId(presentedUser.getId());
        toUpdate.setPassword(encoder.encode(user.getNewPassword()));
        toUpdate.setUpdateBy(UserUtils.getLoginName());
        toUpdate.setUpdateTime(DateUtils.now());
        userMapper.updateById(toUpdate);
        return true;
    }

    @Override
    public IPage<User> pageUsers(User user, int pageNum, int pageSize) {
        IPage<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", YesNoEnum.NO.getCode());
        if (user != null && user.getUsername() != null) {
            queryWrapper.like("username", user.getUsername());
        }
        page.setTotal(userMapper.selectCount(queryWrapper));
        queryWrapper.select(User.columns).orderByDesc("update_time");
        return userMapper.selectPage(page, queryWrapper);
    }
}
