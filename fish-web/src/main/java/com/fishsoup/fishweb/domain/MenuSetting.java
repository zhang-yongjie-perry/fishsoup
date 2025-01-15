package com.fishsoup.fishweb.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("f_menu_setting")
public class MenuSetting implements Serializable {

    @Serial
    private static final long serialVersionUID = 7148039763615765561L;

    @TableId(value = "id", type = ASSIGN_ID)
    private String id;

    private String menuId;

    private String username;

    private Integer sort = 0;

    private String display = "true";

    private Date updateTime;
}
