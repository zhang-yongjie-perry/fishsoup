package com.fishsoup.fishchat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishsoup.fishchat.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}
