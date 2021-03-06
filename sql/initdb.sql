
CREATE DATABASE crawler ;

# create user 'crawler'@'%' IDentified by '123456';
# grant all  privileges on crawler.* to 'crawler'@'%';
USE crawler;
CREATE TABLE `TASK` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `URL` text NOT NULL,
  `REFERRER` text,
  `SEND_TO_MQ_TIME` datetime DEFAULT NULL,
  `NEXT_CRAWL_TIME` datetime DEFAULT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `FINISH_TIME` datetime DEFAULT NULL,
  `FINISH_STATUS` int(11) DEFAULT NULL,
  `REMAINING_RETRY` int(11)  DEFAULT 0,
  `MSG` text,
  `JOB_ID` bigint(20) NOT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `URL` (`URL`(128))
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `JOB` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(100) DEFAULT NULL,
  `DESC` varchar(255) DEFAULT NULL,
  `REQUEST_METHOD` int(11) NOT NULL DEFAULT '0' COMMENT 'get 0,post 1',
  `REQUEST_PARAMS` text COMMENT '请求参数 json 格式',
  `RESPONSE_TYPE` int(11) NOT NULL DEFAULT '0' COMMENT 'html 0,json 1,file 2',
  `MAX_TRY` int(11) NOT NULL DEFAULT '2',
  `MAX_FOLLOW_REDIRECTS` int(11) DEFAULT 0,
  `USER_AGENT` varchar(1024) DEFAULT NULL,
  `TIMEOUT` bigint(20) DEFAULT 10,
  `USE_PROXY` int(11) DEFAULT NULL,
  `HEADERS` text COMMENT 'json 格式',
  `COOKIES` text,
  `RATE` float NOT NULL DEFAULT '1',
  `BURST` int(11) NOT NULL DEFAULT '1',
  `STATUS` int(11) NOT NULL DEFAULT '0',
  `DURATION` BIGINT(20) DEFAULT NULL COMMENT '间隔指定时间定时抓取',
  `OUTPUT_CLASS` varchar(255) NOT NULL,
  `INSERT_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


 CREATE TABLE `PAGE_RULE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DESC` VARCHAR(64) DEFAULT NULL ,
  `NAME` varchar(1024) NOT NULL COMMENT '抽取值的key',
  `TYPE` int(11) NOT NULL COMMENT 'xpath 1,jsonPath 2 ,constants 3',
  `EXPRESSION` varchar(1024) DEFAULT NULL COMMENT '对应type的值,constants需要改字段为json',
  `VALUE_TYPE` int(11) NOT NULL DEFAULT '0',
  `DEFAULT_VALUE` varchar(1024) DEFAULT NULL,
  `NULLABLE` int(11) DEFAULT 0 COMMENT '1不能为null',
  `JOB_ID` bigint(20) NOT NULL,
  `PARENT_ID` BIGINT(20) DEFAULT NULL,
  `ORD` int(11) DEFAULT 0,
  `INSERT_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;