update bus
set created_date='1970-01-01 00:00' 
where created_date is null;

update bus
set changed_date='1970-01-01 00:00' 
where changed_date is null;


ALTER TABLE `bus` 
CHANGE COLUMN `created_date` `created_date` DATETIME NULL DEFAULT  CURRENT_TIMESTAMP ,
CHANGE COLUMN `changed_date` `changed_date` DATETIME NULL DEFAULT  CURRENT_TIMESTAMP ON UPDATE  CURRENT_TIMESTAMP;

