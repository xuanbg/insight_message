
-- ----------------------------
-- Table structure for iml_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `iml_operate_log`;
CREATE TABLE `iml_operate_log` (
  `id` char(32) NOT NULL COMMENT 'UUID主键',
  `tenant_id` char(32) NOT NULL COMMENT '租户ID',
  `type` varchar(16) NOT NULL COMMENT '类型',
  `business_id` char(32) DEFAULT NULL COMMENT '业务ID',
  `business` varchar(16) DEFAULT NULL COMMENT '业务名称',
  `content` json DEFAULT NULL COMMENT '日志内容',
  `dept_id` char(32) DEFAULT NULL COMMENT '创建人部门ID',
  `creator` varchar(64) NOT NULL COMMENT '创建人,系统自动为系统',
  `creator_id` char(32) NOT NULL COMMENT '创建人ID,系统自动为32个0',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_operate_log_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_operate_log_type` (`type`) USING BTREE,
  KEY `idx_operate_log_business_id` (`business_id`) USING BTREE,
  KEY `idx_operate_log_dept_id` (`dept_id`) USING BTREE,
  KEY `idx_operate_log_creator_id` (`creator_id`) USING BTREE,
  KEY `idx_operate_log_created_time` (`created_time`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录表';


-- ----------------------------
-- Table structure for imm_message
-- ----------------------------
DROP TABLE IF EXISTS `imm_message`;
CREATE TABLE `imm_message` (
  `id` char(32) NOT NULL COMMENT 'UUID主键',
  `tenant_id` char(32) NOT NULL COMMENT '租户ID',
  `app_id` char(32) NOT NULL COMMENT '应用Id',
  `tag` varchar(8) NOT NULL COMMENT '消息标签',
  `type` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '发送类型：0、未定义；1、仅消息(001)；2、仅推送(010)；3、推送+消息(011)；4、仅短信(100)；5、消息+短信(101)；6、推送+短信(110)；7、消息+推送+短信(111)',
  `receivers` json DEFAULT NULL COMMENT '接收人，用户ID(推送)/手机号(短信)',
  `title` varchar(64) NOT NULL COMMENT '标题',
  `content` varchar(1024) DEFAULT NULL COMMENT '内容',
  `expire_date` date DEFAULT NULL COMMENT '失效日期',
  `is_broadcast` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否广播消息：0、普通消息；1、广播消息',
  `is_invalid` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否失效：0、正常；1、失效',
  `dept_id` char(32) DEFAULT NULL COMMENT '创建人部门ID',
  `creator` varchar(64) NOT NULL COMMENT '创建人',
  `creator_id` char(32) NOT NULL COMMENT '创建人ID',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_message_tenant_id` (`tenant_id`),
  KEY `idx_message_app_id` (`app_id`),
  KEY `idx_message_tag` (`tag`),
  KEY `idx_message_type` (`type`),
  KEY `idx_message_is_broadcast` (`is_broadcast`),
  KEY `idx_message_dept_id` (`dept_id`),
  KEY `idx_message_creator_id` (`creator_id`),
  KEY `idx_message_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- ----------------------------
-- Table structure for imm_message_push
-- ----------------------------
DROP TABLE IF EXISTS `imm_message_push`;
CREATE TABLE `imm_message_push` (
  `id` char(32) NOT NULL COMMENT 'UUID主键',
  `message_id` char(32) NOT NULL COMMENT '消息ID',
  `user_id` char(32) NOT NULL COMMENT '用户ID',
  `is_read` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已读：0、未读；1、已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  KEY `idx_message_push_message_id` (`message_id`),
  KEY `idx_message_push_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息推送表';

-- ----------------------------
-- Table structure for imm_message_subscribe
-- ----------------------------
DROP TABLE IF EXISTS `imm_message_subscribe`;
CREATE TABLE `imm_message_subscribe` (
  `id` char(32) NOT NULL COMMENT 'UUID主键',
  `message_id` char(32) NOT NULL COMMENT '消息ID',
  `user_id` char(32) NOT NULL COMMENT '用户ID',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_message_subscribe_message_id` (`message_id`),
  KEY `idx_message_subscribe_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息订阅表,只能订阅广播消息';


-- ----------------------------
-- Table structure for ims_scene
-- ----------------------------
DROP TABLE IF EXISTS `ims_scene`;
CREATE TABLE `ims_scene` (
  `id` char(32) NOT NULL COMMENT 'UUID主键',
  `tenant_id` char(32) NOT NULL COMMENT '租户ID',
  `code` char(8) NOT NULL COMMENT '场景遍号',
  `name` varchar(32) NOT NULL COMMENT '场景名称',
  `remark` varchar(256) DEFAULT NULL COMMENT '场景描述',
  `dept_id` char(32) DEFAULT NULL COMMENT '创建人部门ID',
  `creator` varchar(64) NOT NULL COMMENT '创建人',
  `creator_id` char(32) NOT NULL COMMENT '创建人ID',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_scene_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_scene_code` (`code`) USING BTREE,
  KEY `idx_scene_dept_id` (`dept_id`) USING BTREE,
  KEY `idx_scene_creator_id` (`creator_id`) USING BTREE,
  KEY `idx_scene_created_time` (`created_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信发送场景表';

-- ----------------------------
-- Table structure for ims_template
-- ----------------------------
DROP TABLE IF EXISTS `ims_template`;
CREATE TABLE `ims_template` (
  `id` char(32) NOT NULL COMMENT 'UUID主键',
  `tenant_id` char(32) NOT NULL COMMENT '租户ID',
  `code` varchar(8) DEFAULT NULL COMMENT '模板编号',
  `tag` varchar(8) NOT NULL COMMENT '消息标签',
  `type` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '发送类型：0、未定义；1、仅消息(001)；2、仅推送(010)；3、推送+消息(011)；4、仅短信(100)；5、消息+短信(101)；6、推送+短信(110)；7、消息+推送+短信(111)',
  `title` varchar(64) DEFAULT NULL COMMENT '消息标题',
  `content` varchar(1024) NOT NULL COMMENT '消息内容',
  `expire` int(10) unsigned DEFAULT NULL COMMENT '消息有效时长(小时)',
  `remark` varchar(256) DEFAULT NULL COMMENT '备注',
  `is_invalid` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否失效：0、正常；1、失效',
  `dept_id` char(32) DEFAULT NULL COMMENT '创建人部门ID',
  `creator` varchar(64) NOT NULL COMMENT '创建人',
  `creator_id` char(32) NOT NULL COMMENT '创建人ID',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_template_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_template_code` (`code`) USING BTREE,
  KEY `idx_template_dept_id` (`dept_id`) USING BTREE,
  KEY `idx_template_creator_id` (`creator_id`) USING BTREE,
  KEY `idx_template_created_time` (`created_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息模板表';

-- ----------------------------
-- Table structure for ims_scene_template
-- ----------------------------
DROP TABLE IF EXISTS `ims_scene_template`;
CREATE TABLE `ims_scene_template` (
  `id` char(32) NOT NULL COMMENT 'UUID主键',
  `scene_id` char(32) NOT NULL COMMENT '场景ID',
  `template_id` char(32) NOT NULL COMMENT '模板ID',
  `app_id` char(32) DEFAULT NULL COMMENT '应用ID',
  `app_name` varchar(64) DEFAULT NULL COMMENT '应用名称',
  `channel_code` char(4) DEFAULT NULL COMMENT '渠道编码',
  `channel` varchar(64) DEFAULT NULL COMMENT '渠道名称',
  `sign` varchar(16) DEFAULT NULL COMMENT '消息签名',
  `dept_id` char(32) DEFAULT NULL COMMENT '创建人部门ID',
  `creator` varchar(64) NOT NULL COMMENT '创建人',
  `creator_id` char(32) NOT NULL COMMENT '创建人ID',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_scene_template_scene_id` (`scene_id`) USING BTREE,
  KEY `idx_scene_template_template_id` (`template_id`) USING BTREE,
  KEY `idx_scene_template_app_id` (`app_id`) USING BTREE,
  KEY `idx_scene_template_channel_code` (`channel_code`) USING BTREE,
  KEY `idx_scene_template_dept_id` (`dept_id`) USING BTREE,
  KEY `idx_scene_template_creator_id` (`creator_id`) USING BTREE,
  KEY `idx_scene_template_created_time` (`created_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景模板配置表';


-- ----------------------------
-- Table structure for imt_schedule
-- ----------------------------
DROP TABLE IF EXISTS `imt_schedule`;
CREATE TABLE `imt_schedule` (
  `id` char(36) NOT NULL COMMENT 'UUID主键',
  `method` varchar(32) NOT NULL COMMENT '调用方法',
  `task_time` datetime NOT NULL COMMENT '任务开始时间',
  `content` json DEFAULT NULL COMMENT '任务内容',
  `count` int(10) unsigned DEFAULT NULL COMMENT '累计执行次数',
  `is_invalid` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否失效：0、正常；1、失效',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_schedule_created_time` (`created_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务队列表';


-- ----------------------------
-- Table structure for imu_user_device
-- ----------------------------
DROP TABLE IF EXISTS `imu_user_device`;
CREATE TABLE `imu_user_device` (
  `id` char(32) NOT NULL COMMENT '主键(UUID)',
  `user_id` char(32) NOT NULL COMMENT '用户ID',
  `device_id` varchar(64) NOT NULL COMMENT '设备唯一标识',
  `device_info` json DEFAULT NULL COMMENT '设备信息',
  PRIMARY KEY (`id`),
  KEY `idx_user_device_user_id` (`user_id`),
  KEY `idx_user_device_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备表';

-- ----------------------------
-- Table structure for imu_user_tag
-- ----------------------------
DROP TABLE IF EXISTS `imu_user_tag`;
CREATE TABLE `imu_user_tag` (
  `id` char(32) NOT NULL COMMENT '主键(UUID)',
  `user_id` char(32) NOT NULL COMMENT '用户ID',
  `account` varchar(32) DEFAULT NULL COMMENT '用户账号',
  `mobile` varchar(16) DEFAULT NULL COMMENT '用户手机号',
  `tag` varchar(16) DEFAULT NULL COMMENT '标签',
  `dept_id` char(32) DEFAULT NULL COMMENT '创建人部门ID',
  `creator` varchar(64) NOT NULL COMMENT '创建人',
  `creator_id` char(32) NOT NULL COMMENT '创建人ID',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_tag_user_id` (`user_id`),
  KEY `idx_user_tag_account` (`account`),
  KEY `idx_user_tag_mobile` (`mobile`),
  KEY `idx_user_tag_tag` (`tag`),
  KEY `idx_user_tag_dept_id` (`dept_id`),
  KEY `idx_user_tag_creator_id` (`creator_id`),
  KEY `idx_user_tag_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签表';

INSERT INTO `insight_message`.`ims_scene`(`id`, `tenant_id`, `code`, `name`, `remark`, `dept_id`, `creator`, `creator_id`, `created_time`) VALUES 
('27c3a319dc7011e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0001', '验证码登录', NULL, NULL, '系统', '00000000000000000000000000000000', now()),
('27c3a435dc7011e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0002', '验证手机号', NULL, NULL, '系统', '00000000000000000000000000000000', now()),
('27c3a589dc7011e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0003', '新用户注册', NULL, NULL, '系统', '00000000000000000000000000000000', now()),
('27c3a5d2dc7011e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0004', '设置登录密码', NULL, NULL, '系统', '00000000000000000000000000000000', now()),
('27c3a61adc7011e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0005', '设置支付密码', NULL, NULL, '系统', '00000000000000000000000000000000', now()),
('27c3a661dc7011e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0006', '手机解除绑定', NULL, NULL, '系统', '00000000000000000000000000000000', now());

INSERT `ims_template`(`id`, `tenant_id`, `code`, `tag`, `type`, `title`, `content`, `expire`, `remark`, `dept_id`, `creator`, `creator_id`, `created_time`) VALUES 
('387e156ddc7211e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0001', '短信验证码', 4, '验证码登录', '[{code}]是您登录Insight系统的验证码,请在{minutes}分钟内使用【{sign}】', NULL, 'Insight系统登录验证码', NULL, '系统', '00000000000000000000000000000000', now()),
('387e1604dc7211e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0002', '短信验证码', 4, '验证手机号', '[{code}]是您的验证码,请在{minutes}分钟内使用【{sign}】', NULL, 'Insight系统验证手机号验证码', NULL, '系统', '00000000000000000000000000000000', now()),
('387e165adc7211e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0003', '短信验证码', 4, '新用户注册', '[{code}]是您注册Insight系统的验证码,请在{minutes}分钟内使用【{sign}】', NULL, 'Insight系统新用户注册验证码', NULL, '系统', '00000000000000000000000000000000', now()),
('387e16b2dc7211e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0004', '短信验证码', 4, '设置登录密码', '[{code}]是您重设Insight系统登录密码的验证码,请在{minutes}分钟内使用【{sign}】', NULL, 'Insight系统设置登录密码验证码', NULL, '系统', '00000000000000000000000000000000', now()),
('387e1706dc7211e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0005', '短信验证码', 4, '设置支付密码', '[{code}]是您设置Insight系统支付密码的验证码,请在{minutes}分钟内使用【{sign}】', NULL, 'Insight系统设置支付密码验证码', NULL, '系统', '00000000000000000000000000000000', now()),
('387e1759dc7211e9bc200242ac110004', '2564cd559cd340f0b81409723fd8632a', '0006', '短信验证码', 4, '手机解除绑定', '[{code}]是您解除Insight系统绑定手机号的验证码,请在{minutes}分钟内使用【{sign}】', NULL, 'Insight系统手机解除绑定验证码', NULL, '系统', '00000000000000000000000000000000', now());

INSERT `ims_scene_template`(`id`, `scene_id`, `template_id`, `app_id`, `app_name`, `channel_code`, `channel`, `sign`, `dept_id`, `creator`, `creator_id`, `created_time`) VALUES 
(replace(uuid(), '-',''), '27c3a319dc7011e9bc200242ac110004', '387e156ddc7211e9bc200242ac110004', '9dd99dd9e6df467a8207d05ea5581125', '因赛特多租户平台', NULL, NULL, 'Insight', NULL, '系统', '00000000000000000000000000000000', now()),
(replace(uuid(), '-',''), '27c3a435dc7011e9bc200242ac110004', '387e1604dc7211e9bc200242ac110004', '9dd99dd9e6df467a8207d05ea5581125', '因赛特多租户平台', NULL, NULL, 'Insight', NULL, '系统', '00000000000000000000000000000000', now()),
(replace(uuid(), '-',''), '27c3a589dc7011e9bc200242ac110004', '387e165adc7211e9bc200242ac110004', '9dd99dd9e6df467a8207d05ea5581125', '因赛特多租户平台', NULL, NULL, 'Insight', NULL, '系统', '00000000000000000000000000000000', now()),
(replace(uuid(), '-',''), '27c3a5d2dc7011e9bc200242ac110004', '387e16b2dc7211e9bc200242ac110004', '9dd99dd9e6df467a8207d05ea5581125', '因赛特多租户平台', NULL, NULL, 'Insight', NULL, '系统', '00000000000000000000000000000000', now()),
(replace(uuid(), '-',''), '27c3a61adc7011e9bc200242ac110004', '387e1706dc7211e9bc200242ac110004', '9dd99dd9e6df467a8207d05ea5581125', '因赛特多租户平台', NULL, NULL, 'Insight', NULL, '系统', '00000000000000000000000000000000', now()),
(replace(uuid(), '-',''), '27c3a661dc7011e9bc200242ac110004', '387e1759dc7211e9bc200242ac110004', '9dd99dd9e6df467a8207d05ea5581125', '因赛特多租户平台', NULL, NULL, 'Insight', NULL, '系统', '00000000000000000000000000000000', now());
