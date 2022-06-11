
CREATE TABLE `bus_schedule_queue`(
  `id` int(11) NOT NULL AUTO_INCREMENT primary key,
  `bus_id` int(11) NOT NULL,
  `bus_route_id` int(11) NOT NULL,
  `start_time` datetime NOT NULL,
  `travel_time` int(11) NOT NULL,
  `created_date` datetime NOT NULL,
  `changed_date` datetime NOT NULL,
  `status` VARCHAR(10) NOT NULL 
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



CREATE TABLE `web_search_result_log` (
	`id` int(11) NOT NULL AUTO_INCREMENT primary key,
    `search_date` datetime NOT NULL,
    `search_from_city` VARCHAR(100) NOT NULL,
    `search_to_city` VARCHAR(100) NOT NULL,
    `search_from_city_id` int(11) NOT NULL,
    `search_to_city_id` int(11) NOT NULL,
    `search_result_count` int(11) NOT NULL,
    `created_date` datetime NOT NULL 
);

ALTER TABLE aud_booking_payment MODIFY init_parameters TEXT;
