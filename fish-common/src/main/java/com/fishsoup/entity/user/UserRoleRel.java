package com.fishsoup.entity.user;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@TableName("f_user_role_rel")
public class UserRoleRel {

    @TableId(value = "id", type = ASSIGN_ID)
    private String id;

    private String userId;

    private String roleId;
}
