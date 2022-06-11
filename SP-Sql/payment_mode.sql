CREATE DEFINER=`bbkservicesadmin`@`%` FUNCTION `bk_payment_mode`(bk_id int) RETURNS varchar(20) CHARSET utf16
    DETERMINISTIC
BEGIN
	DECLARE payment_mode VARCHAR(20);
    select COALESCE((CASE
                    WHEN
                        (LOCATE('PayAtBus',
                                GROUP_CONCAT(`pr1`.`mode`, `pr1`.`vendor_mode`
                                    SEPARATOR ',')) > 0)
                    THEN
                        'PayAtBus'
                    WHEN
                        (LOCATE('Warrant',
                                GROUP_CONCAT(`pr1`.`mode`, `pr1`.`vendor_mode`
                                    SEPARATOR ',')) > 0)
                    THEN
                        'Warrant'
                    WHEN
                        (LOCATE('Pass',
                                GROUP_CONCAT(`pr1`.`mode`, `pr1`.`vendor_mode`
                                    SEPARATOR ',')) > 0)
                    THEN
                        'Pass'
                    WHEN
                        (LOCATE('Cash',
                                GROUP_CONCAT(`pr1`.`mode`, `pr1`.`vendor_mode`
                                    SEPARATOR ',')) > 0)
                    THEN
                        'Cash'
                    WHEN
                        (LOCATE('Card',
                                GROUP_CONCAT(`pr1`.`mode`, `pr1`.`vendor_mode`
                                    SEPARATOR ',')) > 0)
                    THEN
                        'Card'
                    ELSE GROUP_CONCAT(`pr1`.`mode`, `pr1`.`vendor_mode`
                        SEPARATOR ',')
                END),
                `pr1`.`mode`) AS `payment_modes` into payment_mode
from payment join payment_refund as pr1 on payment.id=pr1.id
where booking_id=bk_id;
    
RETURN payment_mode;
END