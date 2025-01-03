package com.fishsoup.fishweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishsoup.fishweb.domain.MenuDTO;
import com.fishsoup.fishweb.domain.MenuSetting;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuSettingMapper extends BaseMapper<MenuSetting> {

    List<MenuDTO> getPersonalMenus(@Param("username") String username);
}
