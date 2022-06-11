CREATE TABLE `expresslk_bb_web`.`booking_sms_queue` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `booking_reference` VARCHAR(6) NOT NULL,
  `created_date` DATETIME NOT NULL DEFAULT NOW(),
  `modified_date` DATETIME NOT NULL,
  `status` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`id`));
  
  
  
  
  CREATE TABLE `expresslk_bb_web`.`booking_email_queue` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `booking_reference` VARCHAR(6) NOT NULL,
  `created_date` DATETIME NOT NULL DEFAULT NOW(),
  `modified_date` DATETIME NOT NULL,
  `status` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`id`));