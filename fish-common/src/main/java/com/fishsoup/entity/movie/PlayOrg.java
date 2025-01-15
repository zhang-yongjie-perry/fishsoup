package com.fishsoup.entity.movie;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import org.jsoup.select.Elements;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
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
