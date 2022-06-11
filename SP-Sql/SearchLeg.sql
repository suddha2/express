CREATE DEFINER=`bbkservicesadmin`@`%` PROCEDURE `SearchLeg`(IN `p_from_city_id` INT, IN `p_to_city_id` INT, IN `p_departure_time` TIMESTAMP, IN `p_departure_end_time` TIMESTAMP, IN `p_results_count` INT, IN `p_division` DECIMAL(50,0), IN `p_agent_id` INT)
    READS SQL DATA
BEGIN
    DECLARE v_has_allow_all TINYINT(1);
    DECLARE v_has_deny_all  TINYINT(1);
    
    SELECT COUNT(*) > 0 INTO v_has_allow_all
    FROM `agent_restriction`
    WHERE `agent_id` = p_agent_id AND `bus_id` IS NULL AND `type` LIKE 'Allow';
    
    SELECT COUNT(*) > 0 INTO v_has_deny_all
    FROM `agent_restriction`
    WHERE `agent_id` = p_agent_id AND `bus_id` IS NULL AND `type` LIKE 'Deny';

    SELECT 
        `dep`.`schedule_id` AS `scheduleId`, 
        `dep`.`city_id` AS `fromCityId`, 
        `arr`.`city_id` AS `toCityId`, 
        `dep`.`departure_time` AS `departureTime`, 
        `arr`.`arrival_time` AS `arrivalTime`
    FROM 
    (
        SELECT 
            `schedule_id`, 
            `city_id`, 
            `idx`,
            `bus_schedule_bus_stop`.`departure_time`
        FROM `bus_schedule`
            JOIN `bus_schedule_bus_stop`
                ON `bus_schedule`.`id` = `bus_schedule_bus_stop`.`schedule_id`
            JOIN `bus_stop`
                ON `bus_stop`.`id` = `bus_schedule_bus_stop`.`bus_stop_id`
        WHERE `city_id` = p_from_city_id
            AND `bus_schedule_bus_stop`.`departure_time` >= p_departure_time
            AND (p_departure_end_time IS NULL OR `bus_schedule_bus_stop`.`departure_time` < p_departure_end_time)
            AND (p_agent_id IS NULL
                OR (!v_has_allow_all AND !v_has_deny_all)
                OR (v_has_allow_all AND `bus_id` NOT IN (SELECT `bus_id` FROM `agent_restriction` WHERE `agent_id` = p_agent_id AND `type` LIKE 'Deny'))
                OR (v_has_deny_all AND `bus_id` IN (SELECT `bus_id` FROM `agent_restriction` WHERE `agent_id` = p_agent_id AND `type` LIKE 'Allow'))
            )            
            AND `active` = 1
            AND `allowed_divisions` & p_division > 0
    ) AS `dep`
    JOIN 
    (
        SELECT 
            `schedule_id`, 
            `city_id`, 
            `idx`, 
            `bus_schedule_bus_stop`.`arrival_time`
        FROM `bus_schedule`
            JOIN`bus_schedule_bus_stop`
                ON `bus_schedule`.`id` = `bus_schedule_bus_stop`.`schedule_id`
            JOIN `bus_stop`
                ON `bus_stop`.`id` = `bus_schedule_bus_stop`.`bus_stop_id`
        WHERE `city_id` = p_to_city_id
            AND `bus_schedule_bus_stop`.`departure_time` >= p_departure_time
            AND (p_agent_id IS NULL
                OR (!v_has_allow_all AND !v_has_deny_all)
                OR (v_has_allow_all AND `bus_id` NOT IN (SELECT `bus_id` FROM `agent_restriction` WHERE `agent_id` = p_agent_id AND `type` LIKE 'Deny'))
                OR (v_has_deny_all AND `bus_id` IN (SELECT `bus_id` FROM `agent_restriction` WHERE `agent_id` = p_agent_id AND `type` LIKE 'Allow'))
            )    
            AND `active` = 1
            AND `allowed_divisions` & p_division > 0
    ) AS `arr` 
    ON `dep`.`schedule_id` = `arr`.`schedule_id`
    WHERE `dep`.`idx` < `arr`.`idx`
    GROUP BY `scheduleId`
    ORDER BY `dep`.`departure_time` ASC
    LIMIT p_results_count;
END