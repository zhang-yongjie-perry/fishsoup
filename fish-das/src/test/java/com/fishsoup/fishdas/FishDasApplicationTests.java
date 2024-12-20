package com.fishsoup.fishdas;

import com.fishsoup.entity.movie.PlayMovie;
import com.fishsoup.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
class FishDasApplicationTests {

    @Test
    void contextLoads() {
        List<PlayMovie> playMovies = new ArrayList<>();
        playMovies.add(new PlayMovie().setEpisode("第1集"));
        playMovies.add(new PlayMovie().setEpisode("第2集"));
        playMovies.add(new PlayMovie().setEpisode("第5集"));
        playMovies.add(new PlayMovie().setEpisode("第10集"));
        playMovies.add(new PlayMovie().setEpisode("第11集"));
        playMovies.add(new PlayMovie().setEpisode(""));
        playMovies.add(new PlayMovie());
        System.out.println(playMovies.stream().sorted().toList());
    }

}
