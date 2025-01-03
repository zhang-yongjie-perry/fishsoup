package com.fishsoup.fishweb.service;

import com.fishsoup.fishweb.domain.MenuDTO;
import com.fishsoup.fishweb.domain.MenuSetting;

import java.util.List;

@SuppressWarnings("all")
public interface MenuService {

    List<MenuDTO> listPersonalMenus();

    boolean savePersonalMenus(List<MenuSetting> menuSettingList);
}
