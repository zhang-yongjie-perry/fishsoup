package com.fishsoup.entity.movie;

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
@Document("mv_resource")
@Accessors(chain = true)
public class MvResource implements Serializable {

    @Serial
    private static final long serialVersionUID = 7619708512318994948L;
    @Id
    private String id;
    @Field("source_id")
    private String sourceId;
    @Field("m3u8_url")
    private String m3u8Url;
    private String site;
    @Field("create_time")
    private Date createTime;
}
