package com.fishsoup.fishweb.controller;

import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.fishweb.domain.MenuDTO;
import com.fishsoup.fishweb.domain.MenuSetting;
import com.fishsoup.fishweb.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/personal")
    public List<MenuDTO> listMenus() {
        return menuService.listPersonalMenus();
    }

    @PostMapping
    public ResponseResult saveMenuSetting(@RequestBody List<MenuSetting> menuSettings) {
        menuService.savePersonalMenus(menuSettings);
        return ResponseResult.success();
    }
}
