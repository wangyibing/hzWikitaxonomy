USE hzWikiCount2;
-- 
-- Table structure for table `EntityIdTitleTable`
-- 
DROP TABLE IF EXISTS `EntityIdTitle`;
CREATE TABLE `EntityIdTitle` (
	`title` varchar(255) NOT NULL,
	`EntityId` bigint(20) NOT NULL,
	index(`title`), -- mustn't be primary. otherwise, 'À' and 'A' would be same!!
	index(`EntityId`)
)ENGINE=MyISAM DEFAULT CHARSET=utf8;
