create table if not exists `stock` (
	`id` INT UNSIGNED AUTO_INCREMENT,
	`user_id` INT NOT NULL,
	`amount` float,
	`create_at` date,
	`update_at` date,
	PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists `stock_operate_log` (
	`id` INT UNSIGNED AUTO_INCREMENT,
	`user_id` INT NOT NULL,
	`stock_id` int UNSIGNED,
	`operate_amount` float,
	`create_at` date,
	`update_at` date,
	PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
