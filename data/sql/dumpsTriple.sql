USE hzWikiCount2;
-- 
-- Table structure for table `dumpsTriple`
-- 
DROP TABLE IF EXISTS `dumpsTriple`;
CREATE TABLE `dumpsTriple` (
	`id` bigint(20) NOT NULL auto_increment,
	`SubId` int(11) NOT NULL,
	`ObjId` int(11) default NULL,
	`Subject` varchar(255) NOT NULL,
	`Predicate` varchar(255) NOT NULL,
	`Object` longtext default NULL,
	`InfoboxName` text default NULL,

	`Note` text default NULL,
	`Timestamp` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY  (`id`),
	index(`SubId`),
	index(`ObjId`)
)ENGINE=MyISAM DEFAULT CHARSET=utf8;


