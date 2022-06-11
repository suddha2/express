ALTER TABLE `agent_allocation` ADD `division_id` INT NOT NULL AFTER `bus_route_id`, ADD INDEX (`division_id`);
UPDATE agent_allocation SET division_id = 4 WHERE allowed_divisions = 8;
UPDATE agent_allocation SET division_id = 5 WHERE allowed_divisions = 16;
ALTER TABLE `agent_allocation` ADD FOREIGN KEY (`division_id`) REFERENCES `division`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;