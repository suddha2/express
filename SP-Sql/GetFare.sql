CREATE DEFINER=`bbkservicesadmin`@`%` PROCEDURE `GetFare`(IN `p_route_id` INT, IN `p_travel_class_id` INT, IN `p_supplier_group_id` INT, IN `p_supplier_id` INT, IN `p_bus_id` INT, IN `p_from_city_id` INT, IN `p_to_city_id` INT, IN `p_departure_time` TIMESTAMP)
    READS SQL DATA
    DETERMINISTIC
BEGIN
    SET @sector_id = (SELECT `id` FROM `bus_sector` WHERE `bus_route_id` = p_route_id AND `from_city_id` = p_from_city_id AND `to_city_id` = p_to_city_id);

    SELECT *
    FROM (
        SELECT *, 1 AS `priority`
        FROM `bus_fare`
        WHERE `bus_sector_id` = @sector_id
            AND `travel_class_id` = p_travel_class_id
            AND `bus_id` = p_bus_id
            AND `start_time` <= p_departure_time
            AND (`end_time` IS NULL OR `end_time` > p_departure_time)

        UNION

        SELECT *, 2 AS `priority`
        FROM `bus_fare`
        WHERE `bus_sector_id` = @sector_id
            AND `travel_class_id` = p_travel_class_id
            AND `supplier_id` = p_supplier_id
            AND `start_time` <= p_departure_time
            AND (`end_time` IS NULL OR `end_time` > p_departure_time)

        UNION

        SELECT *, 3 AS `priority`
        FROM `bus_fare`
        WHERE `bus_sector_id` = @sector_id
            AND `travel_class_id` = p_travel_class_id
            AND `supplier_group_id` = p_supplier_group_id
            AND `start_time` <= p_departure_time
            AND (`end_time` IS NULL OR `end_time` > p_departure_time)

        UNION

        SELECT *, 4 AS `priority`
        FROM `bus_fare`
        WHERE `bus_sector_id` = @sector_id
            AND `travel_class_id` = p_travel_class_id
            AND `start_time` <= p_departure_time
            AND (`end_time` IS NULL OR `end_time` > p_departure_time)
    ) AS `a`
    ORDER BY `priority` ASC
    LIMIT 1;
END