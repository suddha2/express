ALTER TABLE `expresslk_bb`.`booking_item_passenger` 
ADD COLUMN `passenger_type` VARCHAR(45) NULL DEFAULT 'Adult' AFTER `journey_performed`,
ADD COLUMN `seat_fare` DOUBLE NOT NULL AFTER `passenger_type`;

ALTER TABLE `expresslk_bb`.`aud__booking_item_passenger` 
ADD COLUMN `passenger_type` VARCHAR(45) NULL AFTER `status_code`,
ADD COLUMN `seat_fare` DOUBLE NULL AFTER `passenger_type`;

ALTER TABLE `expresslk_bb`.`booking_item_passenger` 
ADD COLUMN `gross_price` DOUBLE ,
ADD COLUMN `price_before_tax` DOUBLE,
ADD COLUMN `price_before_charge` DOUBLE,
ADD COLUMN `price` DOUBLE,
ADD COLUMN `fare_payment_option` VARCHAR(20) NULL AFTER `passenger_type`;

ALTER TABLE `expresslk_bb`.`aud__booking_item_passenger` 
ADD COLUMN `gross_price` DOUBLE ,
ADD COLUMN `price_before_tax` DOUBLE ,
ADD COLUMN `price_before_charge` DOUBLE ,
ADD COLUMN `price` DOUBLE ,
ADD COLUMN `fare_payment_option` VARCHAR(20) NULL AFTER `passenger_type`;


-- Warrant & Pass SQL Changes.

ALTER TABLE `expresslk_bb`.`payment_refund` 
DROP INDEX `mode` ;