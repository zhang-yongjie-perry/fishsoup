package com.fishsoup.entity.movie;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document("tv_movies")
@Accessors(chain = true)
public class TvMovie implements Serializable {
    @Serial
    private static final long serialVersionUID = -9014321096618068547L;
    @Id
    private String id;
    @Field("sort_num")
    private Integer sortNum;
    private String title;
    @Field("img_url")
    private String imgUrl;
    private String synopsis;
    @Field("play_home_url")
    private String playHomeUrl;
    private Integer status;
    @Field("last_update_time")
    private Date lastUpdateTime;
    @Field("play_orgs")
    private List<PlayOrg> playOrgs;
    private String site;
    @Field("episode_text")
    private String episodeText;

    @Transient
    private String hisPlayOrgName;
    @Transient
    private String hisEpisode;
    @Transient
    private String hisM3u8Url;
    @Transient
    private Integer startTime;
}
