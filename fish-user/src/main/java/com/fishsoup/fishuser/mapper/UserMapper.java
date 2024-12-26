package com.fishsoup.fishuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishsoup.entity.user.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}
