USE hzWikiCount2;
-- 
-- Table structure for table `EntityInfo`
-- 
DROP TABLE IF EXISTS `EntityInfo`;
CREATE TABLE `EntityInfo` (
	`EntityId` bigint(20) NOT NULL,
	`title` varchar(255) NOT NULL,
	`IMGUrl` text default NULL,
	`IMGWidth` bigint(11) default NULL,
	`IMGHeight` bigint(11) default NULL,
	`FirPara` longtext default NULL,
	PRIMARY KEY  (`EntityId`)
)ENGINE=MyISAM DEFAULT CHARSET=utf8;
