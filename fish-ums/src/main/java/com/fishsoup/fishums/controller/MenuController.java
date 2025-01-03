package com.fishsoup.fishums.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.entity.user.Menu;
import com.fishsoup.fishums.feignService.UserFeignService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/menu")
public class MenuController {

    private final UserFeignService userFeignService;

    @GetMapping("/view/list")
    public String menuListView(ModelMap map) {
        map.put("user", SecurityUtils.getSubject().getPrincipal());
        return "menu/menuList";
    }

    @ResponseBody
    @PostMapping("/list")
    public IPage<Menu> pageUsers(Menu menu, @RequestParam("page") int page, @RequestParam("limit") int limit) {
        return userFeignService.pageMenus(menu, page, limit);
    }

    @GetMapping("/add")
    public String addMenuView() {
        return "menu/menuAdd";
    }

    @ResponseBody
    @PostMapping("/add")
    public ResponseResult addMenu(Menu menu) {
        return userFeignService.addMenu(menu);
    }

    @GetMapping("/update/{menuId}")
    public String updateMenuView(@PathVariable("menuId") String menuId, ModelMap map) {
        map.put("menu", userFeignService.findMenuById(menuId));
        return "menu/menuUpdate";
    }

    @ResponseBody
    @PostMapping("/update")
    public ResponseResult updateMenu(Menu menu) {
        return userFeignService.updateMenu(menu);
    }

    @ResponseBody
    @PostMapping("/invalidate")
    public ResponseResult invalidateMenus(@RequestBody List<Menu> menus) {
        return userFeignService.invalidateMenus(menus);
    }

    @ResponseBody
    @PostMapping("/active")
    public ResponseResult activeMenus(@RequestBody List<Menu> menus) {
        return userFeignService.activeMenus(menus);
    }
}
