USE hzWikiCount2;
-- 
-- Table structure for table `hzTriple`
-- 
DROP TABLE IF EXISTS `hzTriple`;
CREATE TABLE `hzTriple` (
	`id` bigint(20) NOT NULL auto_increment,
	`SubId` int(11) NOT NULL,
	`PredId` int(11) NOT NULL,
	`ObjId` int(11) NOT NULL,
	`Subject` varchar(255) NOT NULL,
	`Predicate` varchar(255) NOT NULL,
	`Object` longtext default NULL,
	`Note` longtext default NULL,
	PRIMARY KEY  (`id`),
	index(`SubId`)
)ENGINE=MyISAM DEFAULT CHARSET=utf8;
