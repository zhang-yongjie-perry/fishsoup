package com.fishsoup.entity.user;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fishsoup.enums.UserStatusEnum;
import com.fishsoup.enums.YesNoEnum;
import com.fishsoup.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("f_menu")
public class Menu {

    @TableId(value = "id", type = ASSIGN_ID)
    private String id;

    private String name;

    private String url;

    private String sort;

    /** 权限描述符 系统:模块:功能 fish:video:view */
    private String perms;

    private UserStatusEnum status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @JsonFormat(pattern = DateUtils.YYYY_MM_DD_HH_MI_SS, timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @JsonFormat(pattern = DateUtils.YYYY_MM_DD_HH_MI_SS, timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private YesNoEnum delFlag;
}
