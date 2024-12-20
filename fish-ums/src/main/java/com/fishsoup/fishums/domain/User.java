package com.fishsoup.fishums.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fishsoup.fishums.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("f_user")
@Accessors(chain = true)
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -7526661454203171104L;

    @TableId(value = "id", type = ASSIGN_ID)
    private String id;

    private String username;

    @JsonIgnore
    private String password;

    private String mobilePhone;

    private String email;

    private String avatar;

    private UserTypeEnum type;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

}
