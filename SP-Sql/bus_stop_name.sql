CREATE DEFINER=`bbkservicesadmin`@`%` FUNCTION `bk_bus_stop_name`(`SCHEDULE_ID` INT, `STOP_ID` INT) RETURNS varchar(100) CHARSET utf8
    DETERMINISTIC
BEGIN
DECLARE DEP_TIME VARCHAR(100) DEFAULT "";

    select concat(bs.name,'-',departure_time) into  DEP_TIME
from bus_stop bs join bus_schedule_bus_stop bsbs on bs.id = bsbs.bus_stop_id 
where  bsbs.bus_stop_id=STOP_ID and bsbs.schedule_id=SCHEDULE_ID;
    
RETURN DEP_TIME;
END