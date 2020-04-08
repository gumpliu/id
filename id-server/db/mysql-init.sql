CREATE TABLE `t_alloc` (
  `id` bigint(100) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `biz_tag` varchar(128) NOT NULL DEFAULT '' COMMENT '业务类型',
  `max_id` bigint(20) NOT NULL COMMENT '最大值',
  `step` int(10) NOT NULL DEFAULT '0' COMMENT '号段步长',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` bigint(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;