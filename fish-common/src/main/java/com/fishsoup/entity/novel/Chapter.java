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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("novels")
@Accessors(chain = true)
public class Chapter implements Serializable {
    @Serial
    private static final long serialVersionUID = 646712510356946382L;
    @Id
    private String id;
    private String title;
    private String content;
    @Field("create_time")
    private Date createTime;
}
