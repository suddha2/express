USE `expresslk_bb`;
CREATE 
     OR REPLACE ALGORITHM = UNDEFINED 
    DEFINER = `bbkservicesadmin`@`%` 
    SQL SECURITY DEFINER
VIEW `booking_payments` AS
    SELECT 
        `booking`.`id` AS `id`,
        `booking`.`reference` AS `reference`,
        `booking`.`booking_time` AS `booking_time`,
        `booking`.`expiry_time` AS `expiry_time`,
        `booking`.`chargeable` AS `chargeable`,
        `booking`.`status_code` AS `status_code`,
        `booking`.`cancellation_cause` AS `cancellation_cause`,
        `booking`.`client_id` AS `client_id`,
        `booking`.`agent_id` AS `agent_id`,
        `booking`.`user_id` AS `user_id`,
        `booking`.`remarks` AS `remarks`,
        `booking`.`allowed_divisions` AS `allowed_divisions`,
        SUM(`pr1`.`amount`) AS `total_payments`,
        COALESCE(( CASE WHEN INSTR(group_concat(`pr1`.`mode`,`pr1`.`vendor_mode`) ,'PayAtBus') > 0 THEN 'PayAtBus' 
			 WHEN INSTR(group_concat(`pr1`.`mode`,`pr1`.`vendor_mode`) ,'Warrant') > 0 THEN 'Warrant' 
			 WHEN INSTR(group_concat(`pr1`.`mode`,`pr1`.`vendor_mode`) ,'Pass') > 0 THEN 'Pass' 
             WHEN INSTR(group_concat(`pr1`.`mode`,`pr1`.`vendor_mode`) ,'Cash') > 0 THEN 'Cash' 
             WHEN INSTR(group_concat(`pr1`.`mode`,`pr1`.`vendor_mode`) ,'Card') > 0 THEN 'Card' 
             ELSE group_concat(`pr1`.`mode`,`pr1`.`vendor_mode`)
        END ),`pr1`.`mode`)as payment_modes,
        SUM(`pr2`.`amount`) AS `total_refunds`
    FROM
        ((`booking`
        LEFT JOIN (`payment`
        JOIN `payment_refund` `pr1` ON ((`payment`.`id` = `pr1`.`id`))) ON ((`booking`.`id` = `payment`.`booking_id`)))
        LEFT JOIN (`refund`
        JOIN `payment_refund` `pr2` ON ((`refund`.`id` = `pr2`.`id`))) ON ((`booking`.`id` = `refund`.`booking_id`)))
    GROUP BY `booking`.`id`
    ORDER BY `booking`.`id`;
