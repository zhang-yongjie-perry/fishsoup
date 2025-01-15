package com.fishsoup.entity.user;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fishsoup.enums.AccountStatusEnum;
import com.fishsoup.enums.OnlineStatusEnum;
import com.fishsoup.enums.SexEnum;
import com.fishsoup.enums.YesNoEnum;
import com.fishsoup.util.DateUtils;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@TableName("f_user")
public class User implements Serializable {

    public static final List<String> columns = Arrays.asList("id", "username", "mobile_phone", "email", "avatar", "sex", "account_status", "online_status",
        "last_login_time", "create_by", "create_time", "update_by", "update_time", "version", "del_flag");

    @Serial
    private static final long serialVersionUID = 1221752804820794136L;

    @TableId(value = "id", type = ASSIGN_ID)
    private String id;

    private String username;

    @JSONField(serialize = false)
    private String password;

    private String mobilePhone;

    private String email;

    private String avatar;

    /** 0: 男, 1: 女, 2: 保密 */
    private SexEnum sex;

    /** 0: 失效, 1: 正常, 2: 锁定 */
    private AccountStatusEnum accountStatus;

    /** 0: 离线, 1: 在线 */
    private OnlineStatusEnum onlineStatus;

    private String lastLoginTime;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtils.YYYY_MM_DD_HH_MI_SS, timezone = "GMT+8")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Version
    private Integer version;

    private YesNoEnum delFlag;

    @TableField(exist = false)
    private List<String> perms;

    @TableField(exist = false)
    private String newPassword;
}
