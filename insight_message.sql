-- ----------------------------
-- Table structure for imm_message
-- ----------------------------
DROP TABLE IF EXISTS `imm_message`;
CREATE TABLE `imm_message` (
  `id`                 bigint unsigned   NOT NULL                COMMENT '主键-0',
  `tenant_id`          bigint unsigned            DEFAULT NULL   COMMENT '租户ID',
  `app_id`             bigint unsigned   NOT NULL                COMMENT '应用Id',
  `tag`                varchar(8)        NOT NULL                COMMENT '消息标签',
  `title`              varchar(64)       NOT NULL                COMMENT '标题',
  `content`            varchar(512)               DEFAULT NULL   COMMENT '内容',
  `expire`             int                        DEFAULT NULL   COMMENT '有效时长(分钟)',
  `is_broadcast`       bit               NOT NULL DEFAULT b'0'   COMMENT '是否广播消息：0、普通消息；1、广播消息',
  `creator`            varchar(64)       NOT NULL                COMMENT '创建人',
  `creator_id`         bigint unsigned   NOT NULL                COMMENT '创建人ID',
  `created_time`       datetime          NOT NULL                COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_message_tenant_id` (`tenant_id`),
  KEY `idx_message_app_id` (`app_id`),
  KEY `idx_message_tag` (`tag`),
  KEY `idx_message_is_broadcast` (`is_broadcast`),
  KEY `idx_message_creator_id` (`creator_id`),
  KEY `idx_message_created_time` (`created_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='消息表';

-- ----------------------------
-- Table structure for imm_message_push
-- ----------------------------
DROP TABLE IF EXISTS `imm_message_push`;
CREATE TABLE `imm_message_push` (
  `id`                 bigint unsigned   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `message_id`         bigint unsigned   NOT NULL                COMMENT '消息ID',
  `user_id`            bigint unsigned   NOT NULL                COMMENT '用户ID',
  `is_read`            bit               NOT NULL DEFAULT b'0'   COMMENT '是否已读：0、未读；1、已读',
  `read_time`          datetime          NULL     DEFAULT NULL   COMMENT '阅读时间',
  `is_invalid`         bit               NOT NULL DEFAULT b'0'   COMMENT '是否失效：0、正常；1、失效',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_message_push_message_id` (`message_id`),
  KEY `idx_message_push_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='消息推送表';

-- ----------------------------
-- Table structure for imm_message_subscribe
-- ----------------------------
DROP TABLE IF EXISTS `imm_message_subscribe`;
CREATE TABLE `imm_message_subscribe` (
  `id`                 bigint unsigned   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `message_id`         bigint unsigned   NOT NULL                COMMENT '消息ID',
  `user_id`            bigint unsigned   NOT NULL                COMMENT '用户ID',
  `is_invalid`         bit               NOT NULL DEFAULT b'0'   COMMENT '是否失效：0、正常；1、失效',
  `created_time`       datetime          NOT NULL                COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_message_subscribe_message_id` (`message_id`),
  KEY `idx_message_subscribe_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='消息订阅表,只能订阅广播消息';


-- ----------------------------
-- Table structure for ims_scene
-- ----------------------------
DROP TABLE IF EXISTS `ims_scene`;
CREATE TABLE `ims_scene` (
  `id`                 bigint unsigned   NOT NULL                COMMENT '主键-1',
  `code`               char(8)           NOT NULL                COMMENT '场景遍号',
  `type`               tinyint unsigned  NOT NULL DEFAULT '0'    COMMENT '发送类型：0、未定义；1、仅消息(001)；2、仅推送(010)；3、推送+消息(011)；4、仅短信(100)',
  `name`               varchar(32)       NOT NULL                COMMENT '场景名称',
  `title`              varchar(64)                DEFAULT NULL   COMMENT '消息标题',
  `tag`                varchar(8)                 DEFAULT NULL   COMMENT '消息标签',
  `param`              json                       DEFAULT NULL   COMMENT '消息参数',
  `remark`             varchar(256)               DEFAULT NULL   COMMENT '场景描述',
  `creator`            varchar(64)       NOT NULL                COMMENT '创建人',
  `creator_id`         bigint unsigned   NOT NULL                COMMENT '创建人ID',
  `created_time`       datetime          NOT NULL                COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_scene_code` (`code`) USING BTREE,
  KEY `idx_scene_creator_id` (`creator_id`) USING BTREE,
  KEY `idx_scene_created_time` (`created_time`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='短信发送场景表';

-- ----------------------------
-- Table structure for ims_scene_config
-- ----------------------------
DROP TABLE IF EXISTS `ims_scene_config`;
CREATE TABLE `ims_scene_config` (
  `id`                 bigint unsigned   NOT NULL                COMMENT '主键-2',
  `tenant_id`          bigint unsigned            DEFAULT NULL   COMMENT '租户ID',
  `scene_id`           bigint unsigned   NOT NULL                COMMENT '场景ID',
  `app_id`             bigint unsigned            DEFAULT NULL   COMMENT '应用ID',
  `app_name`           varchar(64)                DEFAULT NULL   COMMENT '应用名称',
  `content`            varchar(512)      NOT NULL                COMMENT '消息内容',
  `sign`               varchar(16)                DEFAULT NULL   COMMENT '消息签名',
  `expire`             int unsigned      NOT NULL DEFAULT 1      COMMENT '消息有效时长(小时)',
  `remark`             varchar(256)               DEFAULT NULL   COMMENT '备注',
  `is_invalid`         bit               NOT NULL DEFAULT b'0'   COMMENT '是否失效：0、正常；1、失效',
  `creator`            varchar(64)       NOT NULL                COMMENT '创建人',
  `creator_id`         bigint unsigned   NOT NULL                COMMENT '创建人ID',
  `created_time`       datetime          NOT NULL                COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_scene_config_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_scene_config_scene_id` (`scene_id`) USING BTREE,
  KEY `idx_scene_config_app_id` (`app_id`) USING BTREE,
  KEY `idx_scene_config_creator_id` (`creator_id`) USING BTREE,
  KEY `idx_scene_config_created_time` (`created_time`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='场景模板配置表';


-- ----------------------------
-- Table structure for imt_schedule
-- ----------------------------
DROP TABLE IF EXISTS `imt_schedule`;
CREATE TABLE `imt_schedule` (
  `id`                 char(36)          NOT NULL                COMMENT '主键-3',
  `type`               tinyint unsigned  NOT NULL DEFAULT '0'    COMMENT '任务类型:0.消息;1.本地调用;2.远程调用',
  `method`             varchar(32)       NOT NULL                COMMENT '调用方法',
  `task_time`          datetime          NOT NULL                COMMENT '任务开始时间',
  `content`            json                       DEFAULT NULL   COMMENT '任务内容',
  `count`              int unsigned               DEFAULT NULL   COMMENT '累计执行次数',
  `expire_time`        datetime                   DEFAULT NULL   COMMENT '任务过期时间',
  `is_invalid`         bit               NOT NULL DEFAULT b'0'   COMMENT '是否失效：0、正常；1、失效',
  `created_time`       datetime          NOT NULL                COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_schedule_created_time` (`created_time`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='任务队列表';


-- ----------------------------
-- Table structure for imu_user_device
-- ----------------------------
DROP TABLE IF EXISTS `imu_user_device`;
CREATE TABLE `imu_user_device` (
  `id`                 bigint unsigned   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`            bigint unsigned   NOT NULL                COMMENT '用户ID',
  `device_id`          varchar(64)       NOT NULL                COMMENT '设备唯一标识',
  `device_info`        json                       DEFAULT NULL   COMMENT '设备信息',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_device_user_id` (`user_id`),
  KEY `idx_user_device_device_id` (`device_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='用户设备表';

-- ----------------------------
-- Table structure for imu_user_tag
-- ----------------------------
DROP TABLE IF EXISTS `imu_user_tag`;
CREATE TABLE `imu_user_tag` (
  `id`                 bigint unsigned   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`            bigint unsigned   NOT NULL                COMMENT '用户ID',
  `account`            varchar(32)                DEFAULT NULL   COMMENT '用户账号',
  `mobile`             varchar(16)                DEFAULT NULL   COMMENT '用户手机号',
  `tag`                varchar(16)                DEFAULT NULL   COMMENT '标签',
  `creator`            varchar(64)       NOT NULL                COMMENT '创建人',
  `creator_id`         bigint unsigned   NOT NULL                COMMENT '创建人ID',
  `created_time`       datetime          NOT NULL                COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_tag_user_id` (`user_id`),
  KEY `idx_user_tag_account` (`account`),
  KEY `idx_user_tag_mobile` (`mobile`),
  KEY `idx_user_tag_tag` (`tag`),
  KEY `idx_user_tag_creator_id` (`creator_id`),
  KEY `idx_user_tag_created_time` (`created_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT ='用户标签表';


-- 初始化场景数据
INSERT ims_scene(id, code, type, `name`, title, tag, `param`, creator, creator_id, created_time) VALUES 
(168140442586578964, '0001', 4, '验证码登录', '验证码登录', '短信验证码', json_array("code", "minutes", "sign"), '系统', 0, now()),
(168140442624327700, '0002', 4, '验证手机号', '验证手机号', '短信验证码', json_array("code", "minutes", "sign"), '系统', 0, now()),
(168140442653687828, '0003', 4, '新用户注册', '新用户注册', '短信验证码', json_array("code", "minutes", "sign"), '系统', 0, now()),
(168140442687242260, '0004', 4, '设置登录密码', '设置登录密码', '短信验证码', json_array("code", "minutes", "sign"), '系统', 0, now()),
(168687422501027860, '0005', 4, '设置支付密码', '设置支付密码', '短信验证码', json_array("code", "minutes", "sign"), '系统', 0, now()),
(168687422542970900, '0006', 4, '手机解除绑定', '手机解除绑定', '短信验证码', json_array("code", "minutes", "sign"), '系统', 0, now());
INSERT ims_scene_config (id, scene_id, content, sign, creator, creator_id, created_time) VALUES 
(168687422589108244, 168140442586578964, '[{code}]是您登录Insight系统的验证码,请在{minutes}分钟内使用【{sign}】', 'Insight', '系统', 0, now()),
(168687422672994324, 168140442624327700, '[{code}]是您的验证码,请在{minutes}分钟内使用【{sign}】', 'Insight', '系统', 0, now()),
(168687422727520276, 168140442653687828, '[{code}]是您注册Insight系统的验证码,请在{minutes}分钟内使用【{sign}】', 'Insight', '系统', 0, now()),
(168687422761074708, 168140442687242260, '[{code}]是您重设Insight系统登录密码的验证码,请在{minutes}分钟内使用【{sign}】', 'Insight', '系统', 0, now()),
(168687422782046228, 168687422501027860, '[{code}]是您设置Insight系统支付密码的验证码,请在{minutes}分钟内使用【{sign}】', 'Insight', '系统', 0, now()),
(168687422803017748, 168687422542970900, '[{code}]是您解除Insight系统绑定手机号的验证码,请在{minutes}分钟内使用【{sign}】', 'Insight', '系统', 0, now());
