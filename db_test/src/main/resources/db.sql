 CREATE TABLE `lock_test` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(20),
  `status` char(1),
  PRIMARY KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 CREATE TABLE `test_11` (
  `id` int(11) NOT NULL,
  `t` date NOT NULL,
  PRIMARY KEY (`id`,`t`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;