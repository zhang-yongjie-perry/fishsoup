package com.fishsoup.fishchat.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fishsoup.fishchat.enums.AccountStatusEnum;
import com.fishsoup.fishchat.enums.OnlineStatusEnum;
import com.fishsoup.fishchat.enums.SexEnum;
import com.fishsoup.fishchat.enums.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("f_user")
public class User implements UserDetails {

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

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return perms.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return !Objects.equals(AccountStatusEnum.EXPIRED, this.accountStatus);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Objects.equals(AccountStatusEnum.LOCKED, this.accountStatus);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Objects.equals(AccountStatusEnum.NORMAL, this.accountStatus);
    }
}
