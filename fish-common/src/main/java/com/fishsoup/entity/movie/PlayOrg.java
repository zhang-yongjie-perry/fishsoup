package com.fishsoup.entity.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import org.jsoup.select.Elements;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PlayOrg {
    private String orgName;
    private String lastEpisode;
    private List<PlayMovie> playList;
    @Transient
    private Elements episodeEls;
}
