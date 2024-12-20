package com.fishsoup.fishweb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("hot_news")
public class HotNews implements Serializable {
    @Serial
    private static final long serialVersionUID = -9014321096618068547L;
    @Id
    private String id;
    private Integer seq;
    private String title;
    private String href;
    private String time;
    private Date createTime;
}
