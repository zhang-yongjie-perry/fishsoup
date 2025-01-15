package com.fishsoup.entity.pic;

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
@Document("pictures")
@Accessors(chain = true)
public class Picture implements Serializable {
    @Serial
    private static final long serialVersionUID = 3008588634100992901L;
    @Id
    private String id;
    private String title;
    /** 0: 轮播, 1: 小图 */
    private Integer type;
    /** 0: 生效, 1: 失效 */
    private Integer status;
    @Field("file_id")
    private String fileId;
    @Field("create_time")
    private Date createTime;
    @Field("thumb_id")
    private String thumbId;
}
