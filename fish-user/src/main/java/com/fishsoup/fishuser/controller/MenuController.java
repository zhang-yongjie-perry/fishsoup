package com.fishsoup.fishuser.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.entity.user.Menu;
import com.fishsoup.fishuser.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/page/list")
    public IPage<Menu> pageMenus(@RequestBody Menu menu, @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        return menuService.pageMenus(menu, pageNum, pageSize);
    }

    @GetMapping
    public List<Menu> listMenus() {
        return menuService.listMenus();
    }

    @PostMapping("/add")
    public ResponseResult addMenu(@RequestBody Menu menu) {
        menuService.addMenu(menu);
        return ResponseResult.success();
    }

    @PostMapping("/update")
    public ResponseResult updateMenu(@RequestBody Menu menu) {
        menuService.updateMenu(menu);
        return ResponseResult.success();
    }

    @GetMapping("/{menuId}")
    public Menu getMenuById(@PathVariable("menuId") String menuId) {
        return menuService.findById(menuId);
    }

    @PostMapping("/invalidate")
    public ResponseResult invalidateMenus(@RequestBody List<Menu> menus) {
        menuService.invalidateMenus(menus.stream().map(Menu::getId).collect(Collectors.toList()));
        return ResponseResult.success();
    }

    @PostMapping("/active")
    public ResponseResult activeMenus(@RequestBody List<Menu> menus) {
        menuService.activeMenus(menus.stream().map(Menu::getId).collect(Collectors.toList()));
        return ResponseResult.success();
    }
}
