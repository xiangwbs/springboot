/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3306
 Source Schema         : boot

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 16/06/2020 09:44:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alipay_bill_record
-- ----------------------------
DROP TABLE IF EXISTS `alipay_bill_record`;
CREATE TABLE `alipay_bill_record` (
  `id` varchar(32) NOT NULL,
  `creator` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modifier` varchar(50) DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  `account_log_id` varchar(256) NOT NULL COMMENT '财务流水号',
  `alipay_order_no` varchar(256) NOT NULL COMMENT '业务流水号',
  `merchant_order_no` varchar(256) NOT NULL COMMENT '商户订单号',
  `paid_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
  `other_account` varchar(255) NOT NULL COMMENT '对方账号',
  `in_amount` decimal(11,2) DEFAULT NULL COMMENT '收入金额',
  `out_amount` decimal(11,2) DEFAULT NULL COMMENT '支出金额',
  `balance` decimal(11,2) NOT NULL COMMENT '账户余额',
  `type` varchar(255) NOT NULL COMMENT '业务类型',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_MERCHANT_ORDER_NO` (`merchant_order_no`),
  KEY `IDX_PAID_DATE` (`paid_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付宝账单记录表';

-- ----------------------------
-- Table structure for data_dict
-- ----------------------------
DROP TABLE IF EXISTS `data_dict`;
CREATE TABLE `data_dict` (
  `id` varchar(50)  NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modifier` varchar(50)  DEFAULT NULL COMMENT '修改人',
  `code` varchar(50)  DEFAULT NULL COMMENT '编码',
  `description` varchar(100)  DEFAULT NULL COMMENT '描述',
  `enable` char(1)  DEFAULT NULL COMMENT '是否启用',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `parent_id` varchar(50)  DEFAULT NULL COMMENT '父键',
  PRIMARY KEY (`id`) ,
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='数据字典';

-- ----------------------------
-- Table structure for demo
-- ----------------------------
DROP TABLE IF EXISTS `demo`;
CREATE TABLE `demo` (
  `id` varchar(255)  NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  `data` text  COMMENT '数据',
  `meat` longblob,
  `blob` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

-- ----------------------------
-- Table structure for file_upload
-- ----------------------------
DROP TABLE IF EXISTS `file_upload`;
CREATE TABLE `file_upload` (
  `id` varchar(50)  NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(50)  DEFAULT NULL COMMENT '创建人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modifier` varchar(50)  DEFAULT NULL COMMENT '修改人',
  `data` longtext  COMMENT '数据',
  `name` varchar(50)  DEFAULT NULL COMMENT '名称',
  `type` varchar(10)  DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`) ,
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='文件上传';

-- ----------------------------
-- Table structure for import_fail_log
-- ----------------------------
DROP TABLE IF EXISTS `import_fail_log`;
CREATE TABLE `import_fail_log` (
  `id` varchar(32)  NOT NULL,
  `creator` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modifier` varchar(50)  DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  `import_Id` varchar(32) NOT NULL COMMENT '导入任务id',
  `content` text  COMMENT '原始数据内容',
  `remark` varchar(50)  DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`),
  KEY `idx_import_id` (`import_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导入失败记录';

-- ----------------------------
-- Table structure for import_task
-- ----------------------------
DROP TABLE IF EXISTS `import_task`;
CREATE TABLE `import_task` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(50)  DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  `modifier` varchar(50)  DEFAULT NULL,
  `file_name` varchar(32)  NOT NULL COMMENT '文件名',
  `total_count` int(11) DEFAULT NULL COMMENT '总条数',
  `fail_count` int(11) DEFAULT NULL COMMENT '失败条数',
  `status` varchar(10)  NOT NULL COMMENT '状态 export fail success',
  `detail` varchar(64)  DEFAULT NULL COMMENT '详情',
  `need_download` tinyint(1) NOT NULL DEFAULT '0' COMMENT '下载',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='导入任务记录';

-- ----------------------------
-- Table structure for sys_authority
-- ----------------------------
DROP TABLE IF EXISTS `sys_authority`;
CREATE TABLE `sys_authority` (
  `id` varchar(50)  NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(50)  DEFAULT NULL COMMENT '创建人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modifier` varchar(50)  DEFAULT NULL COMMENT '修改人',
  `code` varchar(50)  NOT NULL COMMENT '编码',
  `name` varchar(20)  NOT NULL COMMENT '名称',
  `parent_id` varchar(50)  NOT NULL COMMENT '父键',
  `type` int(11) DEFAULT NULL COMMENT '类型',
  `url` varchar(255)  DEFAULT NULL COMMENT 'url',
  `enable` char(1)  DEFAULT NULL COMMENT '是否启用',
  `sort` int(11) DEFAULT NULL COMMENT '序号',
  PRIMARY KEY (`id`) ,
  UNIQUE KEY `uk_code` (`code`) ,
  UNIQUE KEY `uk_sort` (`sort`) ,
  KEY `idx_enable` (`enable`) ,
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='权限';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` varchar(50)  NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(50)  DEFAULT NULL COMMENT '创建人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modifier` varchar(50)  DEFAULT NULL COMMENT '修改人',
  `code` varchar(50)  NOT NULL COMMENT '编码',
  `name` varchar(50)  NOT NULL COMMENT '名称',
  `remark` varchar(100)  DEFAULT NULL COMMENT '描述',
  `enable` char(1)  DEFAULT NULL COMMENT '是否启用',
  PRIMARY KEY (`id`) ,
  UNIQUE KEY `uk_code` (`code`) ,
  KEY `idx_enable` (`enable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='角色';

-- ----------------------------
-- Table structure for sys_role_authority
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_authority`;
CREATE TABLE `sys_role_authority` (
  `id` varchar(50)  NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(50)  DEFAULT NULL COMMENT '创建人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modifier` varchar(50)  DEFAULT NULL COMMENT '修改人',
  `authority_id` varchar(50)  NOT NULL COMMENT '权限id',
  `role_id` varchar(50) NOT NULL COMMENT '角色id',
  PRIMARY KEY (`id`) ,
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='角色权限关系';

-- ----------------------------
-- Table structure for sys_user_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_info`;
CREATE TABLE `sys_user_info` (
  `id` varchar(50)  NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(50)  DEFAULT NULL COMMENT '创建人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modifier` varchar(50)  DEFAULT NULL COMMENT '修改人',
  `mail` varchar(50)  DEFAULT NULL COMMENT '邮箱',
  `name` varchar(50)  NOT NULL COMMENT '名字',
  `password` varchar(50)  DEFAULT NULL COMMENT '密码',
  `salt` varchar(50)  DEFAULT NULL COMMENT '盐值',
  `sex` char(1)  NOT NULL COMMENT '性别',
  `user_name` varchar(20)  NOT NULL COMMENT '用户名',
  `is_admin` char(1)  DEFAULT NULL COMMENT '是否管理员',
  PRIMARY KEY (`id`) ,
  UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='用户';

-- ----------------------------
-- Table structure for sys_user_login_in_out
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_login_in_out`;
CREATE TABLE `sys_user_login_in_out` (
  `id` varchar(50)  NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(50)  DEFAULT NULL COMMENT '创建人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modifier` varchar(50)  DEFAULT NULL COMMENT '修改人',
  `inout_type` int(11) DEFAULT NULL COMMENT '类型',
  `ip` varchar(50)  DEFAULT NULL COMMENT 'ip',
  `user_id` varchar(50)  DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) ,
  KEY `idx_user_id` (`user_id`) ,
  KEY `idx_inout_type` (`inout_type`) ,
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录登出信息';

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` varchar(50)  NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(50)  DEFAULT NULL COMMENT '创建人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modifier` varchar(50)  DEFAULT NULL COMMENT '修改人',
  `role_id` varchar(50)  NOT NULL COMMENT '角色id',
  `user_id` varchar(50)  NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) ,
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='用户角色关系';

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` varchar(50)  NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(50)  DEFAULT NULL COMMENT '创建人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modifier` varchar(50)  DEFAULT NULL COMMENT '修改人',
  `code` varchar(50)  NOT NULL COMMENT '编码',
  `value` text  NOT NULL COMMENT '值',
  `describe` varchar(64)  NOT NULL COMMENT '描述',
  `enable` char(1)  NOT NULL COMMENT '是否启用',
  PRIMARY KEY (`id`) ,
  UNIQUE KEY `uk_code` (`code`) ,
  KEY `idx_enable` (`enable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='系统配置';

-- ----------------------------
-- Table structure for trade_record
-- ----------------------------
DROP TABLE IF EXISTS `trade_record`;
CREATE TABLE `trade_record` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(50) DEFAULT NULL COMMENT '创建人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modifier` varchar(50) DEFAULT NULL COMMENT '修改人',
  `code` varchar(64) DEFAULT NULL COMMENT '网关返回码',
  `msg` varchar(128) DEFAULT NULL COMMENT '网关返回码描述',
  `sub_code` varchar(64) DEFAULT NULL COMMENT '业务返回码',
  `sub_msg` varchar(128) DEFAULT NULL COMMENT '业务返回码描述',
  `out_trade_no` varchar(64) DEFAULT NULL COMMENT '外部交易号',
  `trade_no` varchar(32) NOT NULL COMMENT '交易号',
  `amount` bigint(20) NOT NULL COMMENT '金额',
  `status` varchar(16) NOT NULL COMMENT '状态 PAYING、SUCCESS、FAIL、CLOSED',
  `subject` varchar(64) NOT NULL COMMENT '交易内容',
  `paid_date` datetime DEFAULT NULL COMMENT '支付成功时间',
  `notify_msg` varchar(128) DEFAULT NULL COMMENT '通知信息',
  `notify_status` varchar(16) DEFAULT NULL COMMENT 'notNotified、notified',
  `pay_type` varchar(16) NOT NULL COMMENT '支付类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_trade_no` (`trade_no`),
  KEY `IDX_STATUS_SUB_CODE` (`status`,`sub_code`),
  KEY `IDX_PAID_DATE` (`paid_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交易流水表';
-- ----------------------------
-- Table structure for chat_bi_national_qtr
-- ----------------------------
DROP TABLE IF EXISTS `chat_bi_national_qtr`;
CREATE TABLE `chat_bi_national_qtr`
(
    `ID`              bigint(20)          NOT NULL AUTO_INCREMENT COMMENT '主键(不允许0,默认10000开始)',
    `CATEGORY_PATH`   varchar(100)        NOT NULL COMMENT '分类路径',
    `MEASURE`         varchar(70)         NOT NULL COMMENT '指标',
    `STATISTICS_DATE` int(6)              NOT NULL COMMENT '统计日期',
    `DATA`            decimal(18, 4)      NOT NULL DEFAULT '0.00' COMMENT '数据',
    `DATA_UNIT`       varchar(10)                  DEFAULT NULL COMMENT '数据单位',
    `IS_DELETED`      tinyint(4) UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标记:0=否,1=是',
    `CREATOR`         varchar(30)         NOT NULL COMMENT '创建人',
    `CREATION_DATE`   timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`ID`),
    KEY `IDX_MEASURE` (`MEASURE`),
    UNIQUE KEY `UK_CATEGORY_PATH_MEASURE_STATISTICS_DATE` (`CATEGORY_PATH`, `MEASURE`, `STATISTICS_DATE`)
) ENGINE = InnoDB AUTO_INCREMENT = 10000 DEFAULT CHARSET = utf8 COMMENT '国家季度统计数据';
-- ----------------------------
-- Table structure for xzqh
-- ----------------------------
DROP TABLE IF EXISTS `xzqh`;
CREATE TABLE `xzqh`
(
    `XZQH_DM` varchar(6)   NOT NULL COMMENT '行政区划代码',
    `XZQH_MC`   varchar(150) NOT NULL COMMENT '行政区划名称',
    `SJXZQH_DM` varchar(6) DEFAULT NULL COMMENT '上级行政区划代码',
    `XZQH_CJ`   varchar(1)   NOT NULL COMMENT '行政区划层级',
    PRIMARY KEY (`XZQH_DM`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT ='行政区划表';

SET FOREIGN_KEY_CHECKS = 1;
