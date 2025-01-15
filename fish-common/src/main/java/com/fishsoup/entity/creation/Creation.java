package com.fishsoup.entity.creation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fishsoup.enums.CreationClassifyEnum;
import com.fishsoup.enums.VisibleRangeEnum;
import com.fishsoup.util.DateUtils;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document("creation")
@Accessors(chain = true)
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
    private List<String> tags;
    private String createBy;
    @JsonFormat(pattern = DateUtils.YYYY_MM_DD_HH_MI_SS, timezone = "GMT+8")
    private Date createTime;
    private String updateBy;
    @JsonFormat(pattern = DateUtils.YYYY_MM_DD_HH_MI_SS, timezone = "GMT+8")
    private Date updateTime;
    @Transient
    private List<String> toDelImages;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Creation creation = (Creation) o;
        return Objects.equals(id, creation.id) && Objects.equals(title, creation.title) && Objects.equals(author, creation.author) && Objects.equals(DateUtils.formatDate(time, DateUtils.YYYY_MM_DD), DateUtils.formatDate(creation.time, DateUtils.YYYY_MM_DD)) && Objects.equals(summary, creation.summary) && classify == creation.classify && visibleRange == creation.visibleRange && Objects.equals(content, creation.content) && Objects.equals(tags, creation.tags) && Objects.equals(createBy, creation.createBy) && Objects.equals(DateUtils.formatDate(createTime, DateUtils.YYYY_MM_DD_HH_MI_SS), DateUtils.formatDate(creation.createTime, DateUtils.YYYY_MM_DD_HH_MI_SS)) && Objects.equals(updateBy, creation.updateBy) && Objects.equals(DateUtils.formatDate(updateTime, DateUtils.YYYY_MM_DD_HH_MI_SS), DateUtils.formatDate(creation.updateTime, DateUtils.YYYY_MM_DD_HH_MI_SS));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, DateUtils.formatDate(time, DateUtils.YYYY_MM_DD), summary, classify, visibleRange, content, tags, createBy, DateUtils.formatDate(createTime, DateUtils.YYYY_MM_DD_HH_MI_SS), updateBy, DateUtils.formatDate(updateTime, DateUtils.YYYY_MM_DD_HH_MI_SS));
    }
}
