package com.fishsoup.fishweb.controller;

import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.fishweb.domain.Memo;
import com.fishsoup.fishweb.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/memo")
public class MemoController {

    private final MemoService memoService;

    @PostMapping
    public ResponseResult saveMemo(@RequestBody Memo memo) {
        memoService.saveMemo(memo);
        return ResponseResult.success();
    }

    @GetMapping("/{date}")
    public Memo getMemo(@PathVariable("date") String date) {
        return memoService.getMemo(date);
    }
}
