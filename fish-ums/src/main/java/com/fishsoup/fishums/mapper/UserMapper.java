package com.fishsoup.fishums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishsoup.fishums.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}
