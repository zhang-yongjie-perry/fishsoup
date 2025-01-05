package com.fishsoup.fishweb.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.fishsoup.util.DateUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import static com.fishsoup.fishweb.util.UserUtils.getLoginName;

@Component
public class MyBatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        String loginName = getLoginName();
        metaObject.setValue("createBy", loginName);
        metaObject.setValue("createTime", DateUtils.now());
        metaObject.setValue("updateBy", loginName);
        metaObject.setValue("updateTime", DateUtils.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateBy", getLoginName());
        metaObject.setValue("updateTime", DateUtils.now());
    }
}