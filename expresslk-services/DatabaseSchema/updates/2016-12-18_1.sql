INSERT INTO `report_type` (`id`, `report_name`, `report_type`, `allowed_divisions`) 
VALUES (NULL, 'Agent Detailed', 'AgentDetailed', '31');

INSERT INTO `report_param` (`id`, `report_id`, `name`, `type`, `visible`) 
VALUES (NULL, '7', 'Agent', 'Integer', '1');
INSERT INTO `report_param` (`id`, `report_id`, `name`, `type`, `visible`) 
VALUES (NULL, '7', 'Start time', 'DateTime', '1');
INSERT INTO `report_param` (`id`, `report_id`, `name`, `type`, `visible`) 
VALUES (NULL, '7', 'End time', 'DateTime', '1');