package com.fishsoup.fishweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.entity.http.ResponseResult;
import com.fishsoup.entity.movie.TvMovie;
import com.fishsoup.fishweb.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movie")
public class MovieController {

    private final MovieService movieService;

    @PostMapping("/pageList/{pageNum}/{pageSize}")
    public IPage<TvMovie> pageTvMovies(@RequestBody(required = false) TvMovie conditions, @PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize) {
        return movieService.pageTvMovies(conditions, pageNum, pageSize);
    }

    @GetMapping("/{id}")
    public TvMovie getTvMovieById(@PathVariable("id") String id) throws BusinessException {
        return movieService.findTvMovieById(id);
    }

    @GetMapping("/search/{title}")
    public ResponseResult searchTvMovieByTitle(@PathVariable("title") String title) throws BusinessException {
        movieService.searchTvMovieByTitle(title);
        return ResponseResult.success();
    }

    @GetMapping("/search/nunu/{title}")
    public ResponseResult searchNunuTvMovieByTitle(@PathVariable("title") String title) {
        movieService.searchNunuTvMovieByTitle(title);
        return ResponseResult.success();
    }

    @GetMapping("/m3u8/nunu/{sourceId}")
    public String getNunuM3u8Source(@PathVariable("sourceId") String sourceId) {
        return movieService.getNunuM3u8Resource(sourceId);
    }
}
