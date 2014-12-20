USE hzWikiCount2;
-- 
-- Table structure for table `PredicateInfo`
-- 
DROP TABLE IF EXISTS `PredicateInfo`;
CREATE TABLE `PredicateInfo` (
	`id` bigint(20) NOT NULL auto_increment,
	`Content` varchar(255) NOT NULL,
	`Pinyin` varchar(255) NOT NULL,
	`Freq` bigint(20) NOT NULL,
	`UpperTitles` text default NULL,
	`UpperTitleIds` text default NULL,
	`InfoboxNames` text default NULL,
	`word2vec` text default NULL,
	`LinkIds` text default NULL,
	`SubCates` longtext default NULL,
	`PredCates` longtext default NULL,
	`ObjCates` longtext default NULL,

	index(`Content`), -- mustn't be primary. otherwise, 'À' and 'A' would be same!!
	index(`id`)
)ENGINE=MyISAM DEFAULT CHARSET=utf8;
