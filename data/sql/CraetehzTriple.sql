USE hzWikiCount2;
-- 
-- Table structure for table `hzTriple`
-- 
DROP TABLE IF EXISTS `hzTriple`;
CREATE TABLE `hzTriple` (
	`id` bigint(20) NOT NULL auto_increment,
	`SubId` int(11) NOT NULL,
	`PredId` int(11) default NULL,
	`ObjId` int(11) default NULL,
	`Subject` varchar(255) NOT NULL,
	`Predicate` varchar(255) NOT NULL,
	`Object` longtext default NULL,
	`UpperTitle` text default NULL,
	`OriginalObj` longtext default NULL,
	`OriginalTRtag` longtext default NULL,
	`TRtagid` int(11) NOT NULL,
	`Note` text default NULL,
	`Timestamp` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY  (`id`),
	index(`SubId`),
	index(`PredId`),
	index(`ObjId)
)ENGINE=MyISAM DEFAULT CHARSET=utf8;
