package com.fishsoup.entity.novel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("novels")
@Accessors(chain = true)
public class Novel implements Serializable {
    @Serial
    private static final long serialVersionUID = 3412997422030590948L;
    @Id
    private String id;
    private String name;
    private String status;
    private String author;
    private String summary;
    @Field("poster_url")
    private String posterUrl;
    private String desc;
    @Field("chapter_ids")
    private List<String> chapterIds;
    @Field("create_time")
    private Date createTime;
    @Field("update_time")
    private Date updateTime;
}
