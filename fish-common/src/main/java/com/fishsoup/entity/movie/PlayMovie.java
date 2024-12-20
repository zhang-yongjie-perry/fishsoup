package com.fishsoup.entity.movie;

import com.fishsoup.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PlayMovie implements Comparable<PlayMovie> {
    private String episode;
    private String m3u8Url;
    private String nextM3u8Url;

    @Override
    public int compareTo(PlayMovie o) {
        Integer m = StringUtils.parseInt(this.episode);
        Integer n = StringUtils.parseInt(o.episode);
        if (Objects.equals(m, n)) {
            return 0;
        }
        if (m == null) {
            return 1;
        }
        if (n == null) {
            return -1;
        }
        return m.compareTo(n);
    }
}
