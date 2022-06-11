ALTER TABLE `expresslk_bb_web`.`web_search_result_log` 
ADD COLUMN `domain` LONGTEXT NOT NULL AFTER `created_date`;
