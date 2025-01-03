package com.fishsoup.fishuser.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.user.Menu;

import java.util.List;

public interface MenuService {

    Menu findById(String id);

    boolean addMenu(Menu menu);

    boolean updateMenu(Menu menu);

    IPage<Menu> pageMenus(Menu menu, int pageNum, int pageSize);

    List<Menu> listMenus();

    boolean invalidateMenus(List<String> menuIds);

    boolean activeMenus(List<String> menuIds);
}
