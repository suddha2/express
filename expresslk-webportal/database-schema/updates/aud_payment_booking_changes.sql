ALTER TABLE `aud_booking_payment`
ADD  `user_agent` TEXT NOT NULL AFTER `modified_by`,
ADD  `session_id` TEXT NOT NULL AFTER `user_agent`,
ADD `user_ip` TEXT NOT NULL AFTER `session_id`, 
ADD `request_header` TEXT NOT NULL AFTER `session_id` ;