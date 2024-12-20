package com.fishsoup.fishums.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("f_user_role_rel")
public class UserRoleRel {

    @TableId(value = "id", type = ASSIGN_ID)
    private String id;

    private String userId;

    private String roleId;
}
