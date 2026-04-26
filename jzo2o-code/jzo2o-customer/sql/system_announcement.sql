CREATE TABLE `system_announcement` (
  `id` bigint NOT NULL COMMENT '公告ID',
  `title` varchar(100) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `type` tinyint NOT NULL COMMENT '公告类型：0全体，1客户，2服务人员',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0草稿，1已发布，2已下线',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `offline_time` datetime DEFAULT NULL COMMENT '下线时间',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(64) DEFAULT NULL COMMENT '操作人姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0否，1是',
  PRIMARY KEY (`id`),
  KEY `idx_type_status_publish_time` (`type`, `status`, `publish_time`),
  KEY `idx_status_publish_time` (`status`, `publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

CREATE TABLE `system_announcement_read` (
  `id` bigint NOT NULL COMMENT '记录ID',
  `announcement_id` bigint NOT NULL COMMENT '公告ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_type` tinyint NOT NULL COMMENT '用户类型：1客户，2服务人员',
  `read_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_announcement_user` (`announcement_id`, `user_id`, `user_type`),
  KEY `idx_user_type_user_id` (`user_type`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告已读记录表';
