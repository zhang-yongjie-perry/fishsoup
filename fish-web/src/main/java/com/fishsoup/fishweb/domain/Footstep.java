package com.fishsoup.fishweb.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fishsoup.enums.YesNoEnum;
import com.fishsoup.fishweb.enums.ArtworkTypeEnum;
import com.fishsoup.util.DateUtils;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("f_footstep")
public class Footstep implements Serializable {

    @Serial
    private static final long serialVersionUID = 5107676087395517450L;
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String userId;
    /** yyyy-MM-dd */
    private String today;
    private ArtworkTypeEnum type;
    private String correlationId;
    private String title;
    private String author;
    private String summary;
    private String imageUrl;
    private String playOrgName;
    private String episode;
    private String m3u8Url;
    private Integer startTime;
    private String site;
    private YesNoEnum delFlag;
    private String createBy;
    private Date createTime;
    private String updateBy;
    @JsonFormat(pattern = DateUtils.YYYY_MM_DD_HH_MI_SS, timezone = "GMT+8")
    private Date updateTime;
}
