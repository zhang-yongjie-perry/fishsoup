package com.fishsoup.fishweb.controller;

import com.fishsoup.fishweb.http.ResponseResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class VideoController {

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('fish:video:view')")
    public ResponseResult listVideos() {
        return ResponseResult.success();
    }
}
