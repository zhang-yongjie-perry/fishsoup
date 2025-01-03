package com.fishsoup.fishuser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.user.Menu;
import com.fishsoup.enums.UserStatusEnum;
import com.fishsoup.enums.YesNoEnum;
import com.fishsoup.fishuser.mapper.MenuMapper;
import com.fishsoup.fishuser.service.MenuService;
import com.fishsoup.fishuser.util.UserUtils;
import com.fishsoup.util.DateUtils;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public Menu findById(String id) {
        if (!StringUtils.hasText(id)) {
            throw new BusinessException("菜单id不可为空");
        }
        return menuMapper.selectById(id);
    }

    @Override
    public boolean addMenu(Menu menu) {
        if (!StringUtils.hasText(menu.getName())) {
            throw new BusinessException("菜单名称不可为空");
        }
        if (!StringUtils.hasText(menu.getUrl())) {
            throw new BusinessException("菜单路由不可为空");
        }
        if (!StringUtils.hasText(menu.getSort())) {
            menu.setSort("0");
        }
//        if (menu.getDelFlag() == null) {
//            menu.setDelFlag(YesNoEnum.NO);
//        }
        Long count = menuMapper.selectCount(new QueryWrapper<Menu>().eq("name", menu.getName()));
        if (count > 0) {
            throw new BusinessException("菜单已存在");
        }
        menuMapper.insert(menu);
        return true;
    }

    @Override
    public boolean updateMenu(Menu menu) {
        if (!StringUtils.hasText(menu.getId())) {
            throw new BusinessException("菜单ID不可为空");
        }
        Menu preMenu = menuMapper.selectById(menu.getId());
        if (preMenu == null) {
            throw new BusinessException("菜单不存在");
        }
        if (StringUtils.hasText(menu.getName())) {
            preMenu.setName(menu.getName());
        }
        if (StringUtils.hasText(menu.getUrl())) {
            preMenu.setUrl(menu.getUrl());
        }
        if (StringUtils.hasText(menu.getSort())) {
            preMenu.setSort(menu.getSort());
        }
        if (menu.getStatus() != null) {
            preMenu.setStatus(menu.getStatus());
        }
        if (StringUtils.hasText(menu.getRemark())) {
            preMenu.setRemark(menu.getRemark());
        }
        preMenu.setUpdateBy(UserUtils.getLoginName());
        preMenu.setUpdateTime(DateUtils.now());
        menuMapper.updateById(preMenu);
        return true;
    }

    @Override
    public IPage<Menu> pageMenus(Menu menu, int pageNum, int pageSize) {
        IPage<Menu> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", YesNoEnum.NO.getCode());
        if (menu != null && menu.getName() != null) {
            queryWrapper.like("name", menu.getName());
        }
        page.setTotal(menuMapper.selectCount(queryWrapper));
        queryWrapper.orderByDesc("create_time");
        return menuMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Menu> listMenus() {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", YesNoEnum.NO.getCode());
        queryWrapper.eq("status", UserStatusEnum.NORMAL.getCode());
        queryWrapper.orderByAsc("sort");
        return menuMapper.selectList(queryWrapper);
    }

    @Override
    public boolean invalidateMenus(List<String> menuIds) {
        menuIds.forEach(menuId -> {
            Thread.startVirtualThread(() -> menuMapper.invalidateMenuById(menuId));
        });
        return true;
    }

    @Override
    public boolean activeMenus(List<String> menuIds) {
        menuIds.forEach(menuId -> {
            Thread.startVirtualThread(() -> menuMapper.activeMenuById(menuId));
        });
        return true;
    }
}
