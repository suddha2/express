CREATE TABLE `refund_request` ( 
	`id` INT NOT NULL , 
	`booking_id` INT NOT NULL , 
	`mode_to_refund` VARCHAR(20) NOT NULL , 
	`amount_to_refund` DOUBLE NOT NULL , 
	`payment_mode` VARCHAR(20) NULL , 
	`payment_reference` VARCHAR(255) NULL , 
	`timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , 
	`refunded` TINYINT(1) NOT NULL DEFAULT '0' , 
	PRIMARY KEY (`id`), 
	INDEX (`booking_id`), 
	INDEX (`refunded`)
) ENGINE = InnoDB;

ALTER TABLE `refund_request` ADD FOREIGN KEY (`booking_id`) REFERENCES `booking`(`id`) 
ON DELETE RESTRICT ON UPDATE CASCADE;