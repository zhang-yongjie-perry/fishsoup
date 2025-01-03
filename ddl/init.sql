-- 修改root用户密码
ALTER USER 'root'@'%' IDENTIFIED BY '15269603184_Vae';
GRANT ALL PRIVILEGES ON database_name.* TO 'fish'@'host';

-- db_fish.f_user definition

CREATE TABLE `f_user` (
                          `id` varchar(32) NOT NULL,
                          `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                          `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                          `mobile_phone` varchar(32) DEFAULT NULL,
                          `email` varchar(32) DEFAULT NULL,
                          `avatar` varchar(32) DEFAULT NULL,
                          `sex` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '3' COMMENT '0: 男, 1: 女, 2: 保密, 3: 未知',
                          `account_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '0: 失效, 1: 正常, 2: 锁定',
                          `online_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '0: 离线, 1: 在线',
                          `last_login_time` varchar(32) DEFAULT NULL,
                          `create_by` varchar(32) DEFAULT NULL,
                          `create_time` datetime DEFAULT NULL,
                          `update_by` varchar(32) DEFAULT NULL,
                          `update_time` datetime DEFAULT NULL,
                          `version` int DEFAULT '0',
                          `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '0: 否; 1:是',
                          `type` char(1) DEFAULT '0' NOT NULL COMMENT '0: 普通用户; 1:管理员',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- db_fish.f_user_role_rel definition

CREATE TABLE `f_user_role_rel` (
                                   `id` varchar(32) NOT NULL,
                                   `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                   `role_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- db_fish.f_role_menu_rel definition

CREATE TABLE `f_role_menu_rel` (
                                   `id` varchar(32) NOT NULL,
                                   `role_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                   `menu_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- db_fish.f_role definition

CREATE TABLE `f_role` (
                          `id` varchar(32) NOT NULL,
                          `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                          `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '0: 停用; 1: 正常',
                          `remark` varchar(32) DEFAULT NULL,
                          `create_by` varchar(32) DEFAULT NULL,
                          `create_time` datetime DEFAULT NULL,
                          `update_by` varchar(32) DEFAULT NULL,
                          `update_time` datetime DEFAULT NULL,
                          `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '0: 否; 1: 是',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- db_fish.f_menu definition

CREATE TABLE `f_menu` (
                          `id` varchar(32) NOT NULL,
                          `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                          `url` varchar(32) DEFAULT NULL,
                          `sort` int DEFAULT 0,
                          `perms` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '权限字符串',
                          `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '0: 停用; 1: 正常',
                          `remark` varchar(32) DEFAULT NULL,
                          `create_by` varchar(32) DEFAULT NULL,
                          `create_time` datetime DEFAULT NULL,
                          `update_by` varchar(32) DEFAULT NULL,
                          `update_time` datetime DEFAULT NULL,
                          `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '0: 否; 1: 是',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- db_fish.f_footstep definition

CREATE TABLE `f_footstep` (
                              `id` varchar(32) NOT NULL,
                              `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '0: 文章, 1: 影视, 2: 图片',
                              `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                              `correlation_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0',
                              `create_by` varchar(32) DEFAULT NULL,
                              `create_time` datetime DEFAULT NULL,
                              `update_by` varchar(32) DEFAULT NULL,
                              `update_time` datetime DEFAULT NULL,
                              `today` char(10) NOT NULL COMMENT '格式为年月日',
                              `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                              `author` varchar(20) DEFAULT NULL,
                              `summary` varchar(200) DEFAULT NULL,
                              `image_url` varchar(100) DEFAULT NULL,
                              `play_org_name` varchar(10) DEFAULT NULL,
                              `episode` varchar(16) DEFAULT NULL,
                              `m3u8_url` varchar(100) DEFAULT NULL,
                              `start_time` int NOT NULL DEFAULT 0 COMMENT '视频默认开始播放时间',
                              `site` varchar(30) DEFAULT NULL COMMENT '网站',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `f_footstep_UN` (`type`,`user_id`,`correlation_id`,`del_flag`,`today`),
                              KEY `f_footsteps_user_id_IDX` (`user_id`) USING BTREE,
                              KEY `f_footsteps_del_flag_IDX` (`del_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浏览记录表';

CREATE TABLE db_fish.f_menu_setting (
                                        ID varchar(32) NOT NULL,
                                        menu_id varchar(32) NOT NULL,
                                        username varchar(20) NOT NULL,
                                        sort int DEFAULT 0 NOT NULL,
                                        display varchar() DEFAULT 1 NOT NULL,
                                        update_time date NOT NULL,
                                        CONSTRAINT f_menu_setting_PK PRIMARY KEY (ID),
                                        CONSTRAINT f_menu_setting_UN UNIQUE KEY (menu_id,username)
)
    ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci
COMMENT='菜单个性化设置';

-- 赋权语句
GRANT Alter ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT Create ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT Create view ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT Delete ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT Drop ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT Grant option ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT Index ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT Insert ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT References ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT Select ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT Show view ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT Trigger ON db_fish.f_menu_setting TO 'fish'@'%';
GRANT Update ON db_fish.f_menu_setting TO 'fish'@'%';
