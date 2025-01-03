package com.fishsoup.fishweb.service.impl;

import com.fishsoup.fishweb.domain.MenuDTO;
import com.fishsoup.fishweb.domain.MenuSetting;
import com.fishsoup.fishweb.mapper.MenuSettingMapper;
import com.fishsoup.fishweb.service.MenuService;
import com.fishsoup.fishweb.util.UserUtils;
import com.fishsoup.util.DateUtils;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuSettingMapper menuSettingMapper;

    @Override
    public List<MenuDTO> listPersonalMenus() {
        return menuSettingMapper.getPersonalMenus(UserUtils.getLoginName())
            .stream().sorted().collect(Collectors.toList());
    }

    @Override
    public boolean savePersonalMenus(List<MenuSetting> menuSettingList) {
        if (menuSettingList == null || menuSettingList.isEmpty()) {
            return false;
        }
        String loginName = UserUtils.getLoginName();
        Date now = DateUtils.now();
        // 若有id则根据id修改, 无id则进行新增
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            menuSettingList.forEach(menuSetting -> {
                menuSetting.setUsername(loginName);
                menuSetting.setUpdateTime(now);
                if (StringUtils.hasText(menuSetting.getId())) {
                    executorService.submit(() -> {
                        menuSettingMapper.updateById(menuSetting);
                    });
                    return;
                }
                executorService.submit(() -> {
                    menuSettingMapper.insert(menuSetting);
                });
            });
        }
        return true;
    }
}
