package com.fishsoup.entity.news;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document("hot_news")
@Accessors(chain = true)
public class HotNews implements Serializable {
    @Serial
    private static final long serialVersionUID = -9014321096618068547L;
    @Id
    private String id;
    private Integer seq;
    private String title;
    private String href;
    private String time;
    @Field("create_time")
    private Date createTime;
    private String site;
    @Field("news_type")
    private String newsType;
}
