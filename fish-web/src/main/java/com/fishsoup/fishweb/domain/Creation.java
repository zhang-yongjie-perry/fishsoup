package com.fishsoup.fishweb.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fishsoup.fishweb.enums.CreationClassifyEnum;
import com.fishsoup.fishweb.enums.VisibleRangeEnum;
import com.fishsoup.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("creation")
public class Creation implements Serializable {
    @Serial
    private static final long serialVersionUID = -4923144759626693296L;
    @Id
    private String id;
    private String title;
    private String author;
    @JsonFormat(pattern = DateUtils.YYYY_MM_DD, timezone = "GMT+8")
    private Date time;
    private String summary;
    /** 文档分类 */
    private CreationClassifyEnum classify;
    /** 可见范围 */
    private VisibleRangeEnum visibleRange;
    private String content;
    private String createBy;
    @JsonFormat(pattern = DateUtils.YYYY_MM_DD_HH_MI_SS, timezone = "GMT+8")
    private Date createTime;
    private String updateBy;
    @JsonFormat(pattern = DateUtils.YYYY_MM_DD_HH_MI_SS, timezone = "GMT+8")
    private Date updateTime;
    @Transient
    private List<String> toDelImages;
}
