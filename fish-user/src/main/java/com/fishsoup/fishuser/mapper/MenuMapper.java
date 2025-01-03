package com.fishsoup.fishuser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishsoup.entity.user.Menu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuMapper extends BaseMapper<Menu> {

    @Select("SELECT m.perms FROM f_user u " +
        "INNER JOIN f_user_role_rel ur ON ur.user_id = u.id " +
        "INNER JOIN f_role r ON r.id = ur.role_id " +
        "INNER JOIN f_role_menu_rel rm ON rm.role_id = r.id " +
        "INNER JOIN f_menu m ON m.id = rm.menu_id " +
        "WHERE u.id = #{userId}")
    List<String> listPermsByUserId(String userId);

    @Update("UPDATE f_menu SET STATUS = '0' WHERE ID = #{id}")
    void invalidateMenuById(@Param("id") String id);

    @Update("UPDATE f_menu SET STATUS = '1' WHERE ID = #{id}")
    void activeMenuById(@Param("id") String id);
}
