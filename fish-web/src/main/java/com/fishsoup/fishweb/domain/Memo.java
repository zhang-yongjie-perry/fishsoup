package com.fishsoup.fishweb.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Document("memo")
public class Memo implements Serializable {
    @Serial
    private static final long serialVersionUID = 8159808683923071117L;
    @Id
    private String id;
    private String username;
    private String date;
    private String color;
    private String content;
    private Date updateTime;
}
