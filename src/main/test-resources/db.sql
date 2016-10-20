CREATE TABLE `data_definition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key` varchar(45) NOT NULL,
  `definition_type` varchar(45) NOT NULL COMMENT 'select,input,checkbox, multiselect',
  `data_type` varchar(45) NOT NULL COMMENT 'int,string,boolean,datetime',
  `is_tag` tinyint(4) NOT NULL,
  `tag_options` varchar(500) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `rule` varchar(45) DEFAULT NULL,
  `data_options` varchar(45) DEFAULT NULL COMMENT '字段选项',
  `template` varchar(1000) DEFAULT NULL COMMENT 'freemarker 模板用来展示字段值',
  `is_use_template` tinyint(4) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `data_definition_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `left_id` varchar(45) NOT NULL,
  `dd_ref_id` varchar(45) NOT NULL,
  `int_value` bigint(20) NOT NULL,
  `str_value` varchar(45) NOT NULL,
  `feature` text NOT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `relation_index` (`str_value`,`int_value`,`dd_ref_id`,`left_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

