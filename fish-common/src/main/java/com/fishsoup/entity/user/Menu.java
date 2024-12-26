package com.fishsoup.entity.user;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fishsoup.enums.UserStatusEnum;
import com.fishsoup.enums.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("f_menu")
public class Menu {

    @TableId(value = "id", type = ASSIGN_ID)
    private String id;

    private String name;

    private String url;

    private String route;

    /** 权限描述符 系统:模块:功能 fish:video:view */
    private String perms;

    private UserStatusEnum status;

    private String remark;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private YesNoEnum delFlag;
}
