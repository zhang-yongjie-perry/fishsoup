package com.fishsoup.fishweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.fishweb.domain.Footstep;
import com.fishsoup.fishweb.enums.ArtworkTypeEnum;
import com.fishsoup.fishweb.service.FootstepService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/footstep")
public class FootstepController {

    private final FootstepService footstepService;

    @GetMapping("/page/{artworkType}/{pageNum}/{pageSize}")
    public IPage<Footstep> pageFootsteps(@PathVariable("artworkType") String artworkType,
        @PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize) {
        return footstepService.pageFootsteps(ArtworkTypeEnum.valueOf(artworkType), pageNum, pageSize);
    }

    @PostMapping()
    public ResponseResult saveFootstep(@RequestBody Footstep footstep) {
        footstepService.saveFootstep(footstep);
        return ResponseResult.success();
    }
}
