package com.fishsoup.fishweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishsoup.fishweb.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}
