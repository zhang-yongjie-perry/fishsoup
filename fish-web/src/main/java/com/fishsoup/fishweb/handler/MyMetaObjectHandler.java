package com.fishsoup.fishweb.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.fishsoup.fishweb.util.DateUtils;
import com.fishsoup.fishweb.util.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createBy", SecurityUtils.getLoginName());
        metaObject.setValue("createTime", DateUtils.now());
        metaObject.setValue("updateBy", SecurityUtils.getLoginName());
        metaObject.setValue("updateTime", DateUtils.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateBy", SecurityUtils.getLoginName());
        metaObject.setValue("updateTime", DateUtils.now());
    }
}
