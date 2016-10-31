use prm;

CREATE TABLE `prm_key_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `source_key` varchar(45) NOT NULL,
  `key` varchar(45) NOT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`source_key`,`key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE `prm_data_options` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `d_id` bigint(20) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL COMMENT '0: 静态,\n1: 动态',
  `parent_id` bigint(20) DEFAULT NULL,
  `option_value` varchar(45) DEFAULT NULL,
  `option` varchar(45) DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `statistic_keys` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key` varchar(45) DEFAULT NULL COMMENT '指标唯一标识符,不能重复，字母或者数字组成',
  `nick_name` varchar(100) DEFAULT NULL COMMENT '字段展示的名稱',
  `definition_type` varchar(45) NOT NULL COMMENT '指标页面展示类型(datetime,input,multiselect,select,other)',
  `data_type` varchar(45) NOT NULL COMMENT '表示指标的数据类型(double, int, string, text)',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父指标id',
  `type` varchar(100) DEFAULT '0' COMMENT '指标类型(0.普通指标; 1:标签指标; 2: 备注指标)',
  `rule` varchar(45) DEFAULT NULL COMMENT '指标正则表达式',
  `template` varchar(200) DEFAULT NULL COMMENT '指标的converter函数，一些指标在渲染时需要进行特殊处理，在这里配置具体的处理函数',
  `is_use_template` tinyint(4) DEFAULT '0' COMMENT '是否使用模板',
  `child_comment_name` varchar(40) DEFAULT NULL COMMENT '备注指标的key',
  `update_type` tinyint(4) DEFAULT NULL COMMENT '指标同步控制(0: 只更新CRM数据;1: 反向会更新业务的数据)',
  `unit` varchar(45) DEFAULT NULL COMMENT '单位列',
  `rank` tinyint(4) DEFAULT NULL COMMENT '1 or 2',
  `is_left_data` tinyint(4) DEFAULT '0' COMMENT '是否为左表字段指标',
  `left_col_name` varchar(45) DEFAULT NULL COMMENT '左表指标列的名称',
  `status` tinyint(4) DEFAULT '0' COMMENT '-1废除\n0启用',
  `source_data` varchar(200) DEFAULT NULL COMMENT '来源业务数据库的dbname:tablename:column',
  `computation_rule` varchar(1000) DEFAULT NULL COMMENT '指标对应的规则 eg (and (> key1 10) (< key2 20))',
  `computation_json` text COMMENT '指标对应的json规则 ',
  `tag_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0: 默认值; 1:  系统标签 2: 筛选打标 3: 动态打标 4: 组标签',
  `description` varchar(200) DEFAULT NULL,
  `is_editable` tinyint(4) DEFAULT '1',
  `is_required` tinyint(4) DEFAULT '0' COMMENT '是否必填\n0:非必填；1:必填',
  `is_query` tinyint(4) DEFAULT '0' COMMENT '1,支持筛选\n0,不支持筛选',
  `emp_name` varchar(45) DEFAULT NULL COMMENT '最后一次操作人账号',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=utf8;

CREATE TABLE `prm_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'prm 用户id',
  `user_key` varchar(45) DEFAULT NULL COMMENT '来源渠道的用户唯一标识符',
  `source` varchar(45) NOT NULL,
  `emp_name` varchar(45) DEFAULT NULL COMMENT '操作人',
  `status` tinyint(4) DEFAULT '0' COMMENT '0,正常\n-1,删除',
  `dd2` varchar(45) DEFAULT NULL,
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dp_key` (`user_key`,`source`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

CREATE TABLE `data_definition_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `prm_id` bigint(20) NOT NULL,
  `dd_ref_id` varchar(45) NOT NULL,
  `int_value` bigint(20) DEFAULT NULL,
  `str_value` varchar(45) DEFAULT NULL,
  `double_value` double DEFAULT NULL,
  `feature` text,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint(4) DEFAULT '0' COMMENT '0: 正常访问\n-1: 删除',
  `emp_name` varchar(45) DEFAULT NULL COMMENT '操作人记录',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dp_key` (`prm_id`,`dd_ref_id`),
  KEY `double_index` (`dd_ref_id`,`double_value`),
  KEY `str_index` (`str_value`,`dd_ref_id`),
  KEY `int_index` (`int_value`,`dd_ref_id`),
  KEY `left_id_key` (`prm_id`)
) ENGINE=MyISAM AUTO_INCREMENT=399984 DEFAULT CHARSET=utf8;
