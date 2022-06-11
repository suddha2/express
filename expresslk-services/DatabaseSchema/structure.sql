-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Host: express-db.cm8ispqmkrpk.ap-southeast-1.rds.amazonaws.com:3306
-- Generation Time: Dec 12, 2016 at 02:59 AM
-- Server version: 5.7.11-log
-- PHP Version: 5.5.9-1ubuntu4.20

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `expresslk_bb`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`bbkservicesadmin`@`%` PROCEDURE `GetFare` (IN `p_route_id` INT, IN `p_travel_class_id` INT, IN `p_supplier_group_id` INT, IN `p_supplier_id` INT, IN `p_bus_id` INT, IN `p_from_city_id` INT, IN `p_to_city_id` INT, IN `p_departure_time` TIMESTAMP)  READS SQL DATA
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
END$$

CREATE DEFINER=`bbkservicesadmin`@`%` PROCEDURE `SearchLeg` (IN `p_from_city_id` INT, IN `p_to_city_id` INT, IN `p_departure_time` TIMESTAMP, IN `p_departure_end_time` TIMESTAMP, IN `p_results_count` INT, IN `p_division` DECIMAL(50,0), IN `p_agent_id` INT)  READS SQL DATA
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
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `account_status`
--

CREATE TABLE `account_status` (
  `id` int(11) NOT NULL,
  `code` varchar(4) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `agent`
--

CREATE TABLE `agent` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `agent_allocation`
--

CREATE TABLE `agent_allocation` (
  `id` int(11) NOT NULL,
  `bus_route_id` int(11) DEFAULT NULL,
  `agent_id` int(11) NOT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `agent_allocation_seat`
--

CREATE TABLE `agent_allocation_seat` (
  `agent_allocation_id` int(11) NOT NULL,
  `seat_number` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `agent_restriction`
--

CREATE TABLE `agent_restriction` (
  `id` int(11) NOT NULL,
  `agent_id` int(11) NOT NULL,
  `bus_id` int(11) DEFAULT NULL,
  `type` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `amenity`
--

CREATE TABLE `amenity` (
  `id` int(11) NOT NULL,
  `code` varchar(4) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `api_token`
--

CREATE TABLE `api_token` (
  `id` int(11) NOT NULL,
  `token` varchar(50) NOT NULL,
  `username` varchar(50) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expiry` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__booking`
--

CREATE TABLE `aud__booking` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `booking_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expiry_time` datetime DEFAULT NULL,
  `chargeable` double DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `client_id` int(11) DEFAULT NULL,
  `status_code` int(11) DEFAULT NULL,
  `cancellation_cause` varchar(50) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `agent_id` int(11) DEFAULT NULL,
  `remarks` text,
  `division_id` int(11) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) DEFAULT NULL,
  `write_allowed_divisions` decimal(50,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__booking_item`
--

CREATE TABLE `aud__booking_item` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `cost` double DEFAULT NULL,
  `gross_price` double DEFAULT NULL,
  `idx` int(11) DEFAULT NULL,
  `fare` double NOT NULL,
  `price` double DEFAULT NULL,
  `price_before_tax` double DEFAULT NULL,
  `price_before_charge` double DEFAULT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `from_bus_stop_id` int(11) DEFAULT NULL,
  `schedule_id` int(11) DEFAULT NULL,
  `status_code` int(11) DEFAULT NULL,
  `cancellation_cause` varchar(50) DEFAULT NULL,
  `to_bus_stop_id` int(11) DEFAULT NULL,
  `remarks` varchar(100) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) DEFAULT NULL,
  `write_allowed_divisions` decimal(50,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__booking_item_cancellation`
--

CREATE TABLE `aud__booking_item_cancellation` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `booking_item_id` int(11) DEFAULT NULL,
  `cancellation_scheme_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__booking_item_charge`
--

CREATE TABLE `aud__booking_item_charge` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `charge_scheme_id` int(11) DEFAULT NULL,
  `booking_item_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__booking_item_discount`
--

CREATE TABLE `aud__booking_item_discount` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `discount_scheme_id` int(11) DEFAULT NULL,
  `booking_item_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__booking_item_markup`
--

CREATE TABLE `aud__booking_item_markup` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `is_margin` tinyint(1) DEFAULT NULL,
  `markup_scheme_id` int(11) DEFAULT NULL,
  `booking_item_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__booking_item_passenger`
--

CREATE TABLE `aud__booking_item_passenger` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `journey_performed` bit(1) DEFAULT NULL,
  `seat_number` varchar(255) DEFAULT NULL,
  `booking_item_id` int(11) DEFAULT NULL,
  `passenger_id` int(11) DEFAULT NULL,
  `status_code` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__booking_item_tax`
--

CREATE TABLE `aud__booking_item_tax` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `tax_scheme_id` int(11) DEFAULT NULL,
  `booking_item_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__bus`
--

CREATE TABLE `aud__bus` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) DEFAULT NULL,
  `cash_on_departure_allowed` bit(1) DEFAULT NULL,
  `notification_method` varchar(50) DEFAULT NULL,
  `contact` varchar(255) DEFAULT NULL,
  `admin_contact` varchar(50) DEFAULT NULL,
  `markup_only` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `permit_expiry_date` date DEFAULT NULL,
  `permit_issue_date` date DEFAULT NULL,
  `permit_number` varchar(255) DEFAULT NULL,
  `plate_number` varchar(255) DEFAULT NULL,
  `seconds_before_tb_end` int(11) DEFAULT NULL,
  `seconds_before_ticketing_active` int(11) DEFAULT NULL,
  `seconds_before_web_end` int(11) DEFAULT NULL,
  `bus_type_id` int(11) DEFAULT NULL,
  `conductor_id` int(11) DEFAULT NULL,
  `division_id` int(11) DEFAULT NULL,
  `driver_id` int(11) DEFAULT NULL,
  `seating_profile_id` int(11) DEFAULT NULL,
  `rating` float DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `travel_class_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__bus_amenity`
--

CREATE TABLE `aud__bus_amenity` (
  `REV` int(11) NOT NULL,
  `bus_id` int(11) NOT NULL,
  `amenity_id` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__bus_bus_route`
--

CREATE TABLE `aud__bus_bus_route` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `bus_id` int(11) DEFAULT NULL,
  `load_factor` float DEFAULT NULL,
  `bus_route_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__change`
--

CREATE TABLE `aud__change` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `booking_item_id` int(11) DEFAULT NULL,
  `booking_item_passenger_id` int(11) DEFAULT NULL,
  `change_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` text,
  `user_id` int(11) DEFAULT NULL,
  `change_type_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__conductor`
--

CREATE TABLE `aud__conductor` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) DEFAULT NULL,
  `nick_name` varchar(255) DEFAULT NULL,
  `ntc_registration_expiry_date` date DEFAULT NULL,
  `ntc_registration_number` varchar(255) DEFAULT NULL,
  `person_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__driver`
--

CREATE TABLE `aud__driver` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) DEFAULT NULL,
  `driving_licence` varchar(255) DEFAULT NULL,
  `driving_licence_expiry_date` date DEFAULT NULL,
  `driving_licence_issue_date` date DEFAULT NULL,
  `nick_name` varchar(255) DEFAULT NULL,
  `ntc_registration_expiry_date` date DEFAULT NULL,
  `ntc_registration_number` varchar(255) DEFAULT NULL,
  `person_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__passenger`
--

CREATE TABLE `aud__passenger` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `idx` int(11) DEFAULT NULL,
  `passenger_type` varchar(255) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `nic` varchar(12) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `email` varchar(75) DEFAULT NULL,
  `mobile_telephone` varchar(20) DEFAULT NULL,
  `booking_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__payment`
--

CREATE TABLE `aud__payment` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) DEFAULT NULL,
  `write_allowed_divisions` decimal(50,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__payment_refund`
--

CREATE TABLE `aud__payment_refund` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `actual_amount` double DEFAULT NULL,
  `actual_currency` varchar(255) DEFAULT NULL,
  `mode` varchar(255) DEFAULT NULL,
  `vendor_mode` varchar(20) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__person`
--

CREATE TABLE `aud__person` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `home_telephone` varchar(255) DEFAULT NULL,
  `house_number` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `mobile_telephone` varchar(255) DEFAULT NULL,
  `nic` varchar(255) DEFAULT NULL,
  `postal_code` int(11) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `work_telephone` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `aud__refund`
--

CREATE TABLE `aud__refund` (
  `id` int(11) NOT NULL,
  `REV` int(11) NOT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) DEFAULT NULL,
  `write_allowed_divisions` decimal(50,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `booking`
--

CREATE TABLE `booking` (
  `id` int(11) NOT NULL,
  `reference` varchar(50) NOT NULL,
  `booking_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expiry_time` timestamp NULL DEFAULT NULL,
  `chargeable` double NOT NULL,
  `status_code` varchar(4) NOT NULL,
  `cancellation_cause` varchar(50) DEFAULT NULL,
  `client_id` int(11) NOT NULL,
  `agent_id` int(11) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `remarks` text,
  `division_id` int(11) NOT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0',
  `write_allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `booking_item`
--

CREATE TABLE `booking_item` (
  `id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `idx` int(11) NOT NULL,
  `fare` double NOT NULL,
  `cost` double NOT NULL,
  `gross_price` double NOT NULL,
  `price_before_tax` double NOT NULL,
  `price_before_charge` double NOT NULL,
  `price` double NOT NULL,
  `status_code` varchar(4) NOT NULL,
  `cancellation_cause` varchar(50) DEFAULT NULL,
  `schedule_id` int(11) NOT NULL,
  `from_bus_stop_id` int(11) NOT NULL,
  `to_bus_stop_id` int(11) NOT NULL,
  `remarks` varchar(100) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0',
  `write_allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `booking_item_cancellation`
--

CREATE TABLE `booking_item_cancellation` (
  `id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `booking_item_id` int(11) DEFAULT NULL,
  `cancellation_scheme_id` int(11) NOT NULL,
  `amount` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Records a change in the booking or a booking item';

-- --------------------------------------------------------

--
-- Table structure for table `booking_item_charge`
--

CREATE TABLE `booking_item_charge` (
  `id` int(11) NOT NULL,
  `booking_item_id` int(11) NOT NULL,
  `charge_scheme_id` int(11) NOT NULL,
  `amount` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `booking_item_discount`
--

CREATE TABLE `booking_item_discount` (
  `id` int(11) NOT NULL,
  `booking_item_id` int(11) NOT NULL,
  `discount_scheme_id` int(11) NOT NULL,
  `amount` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `booking_item_markup`
--

CREATE TABLE `booking_item_markup` (
  `id` int(11) NOT NULL,
  `booking_item_id` int(11) NOT NULL,
  `markup_scheme_id` int(11) NOT NULL,
  `amount` double NOT NULL,
  `is_margin` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `booking_item_passenger`
--

CREATE TABLE `booking_item_passenger` (
  `id` int(11) NOT NULL,
  `booking_item_id` int(11) NOT NULL,
  `passenger_id` int(11) NOT NULL,
  `seat_number` varchar(10) DEFAULT NULL,
  `status_code` varchar(4) NOT NULL,
  `journey_performed` bit(1) NOT NULL DEFAULT b'0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `booking_item_tax`
--

CREATE TABLE `booking_item_tax` (
  `id` int(11) NOT NULL,
  `booking_item_id` int(11) NOT NULL,
  `tax_scheme_id` int(11) NOT NULL,
  `amount` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Stand-in structure for view `booking_light`
-- (See below for the actual view)
--
CREATE TABLE `booking_light` (
`id` int(11)
,`reference` varchar(50)
,`booking_time` timestamp
,`expiry_time` timestamp
,`chargeable` double
,`status_code` varchar(4)
,`cancellation_cause` varchar(50)
,`client_id` int(11)
,`agent_id` int(11)
,`user_id` int(11)
,`remarks` text
,`division_id` int(11)
,`allowed_divisions` decimal(50,0)
,`write_allowed_divisions` decimal(50,0)
,`client_nic` varchar(12)
,`client_mobile_telephone` varchar(20)
,`client_name` varchar(100)
,`from_city` varchar(100)
,`to_city` varchar(100)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `booking_payments`
-- (See below for the actual view)
--
CREATE TABLE `booking_payments` (
`id` int(11)
,`reference` varchar(50)
,`booking_time` timestamp
,`expiry_time` timestamp
,`chargeable` double
,`status_code` varchar(4)
,`cancellation_cause` varchar(50)
,`client_id` int(11)
,`agent_id` int(11)
,`user_id` int(11)
,`remarks` text
,`allowed_divisions` decimal(50,0)
,`total_payments` double
,`payment_modes` text
,`total_refunds` double
);

-- --------------------------------------------------------

--
-- Table structure for table `booking_status`
--

CREATE TABLE `booking_status` (
  `id` int(11) NOT NULL,
  `code` varchar(4) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus`
--

CREATE TABLE `bus` (
  `id` int(11) NOT NULL,
  `plate_number` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `bus_type_id` int(11) NOT NULL,
  `travel_class_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `division_id` int(11) NOT NULL,
  `markup_only` tinyint(1) NOT NULL DEFAULT '0',
  `cash_on_departure_allowed` tinyint(1) NOT NULL DEFAULT '0',
  `notification_method` varchar(50) NOT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0',
  `contact` varchar(50) DEFAULT NULL,
  `admin_contact` varchar(50) DEFAULT NULL,
  `driver_id` int(11) DEFAULT NULL,
  `conductor_id` int(11) DEFAULT NULL,
  `seating_profile_id` int(11) DEFAULT NULL,
  `rating` float DEFAULT NULL,
  `seconds_before_tb_end` int(11) DEFAULT NULL,
  `seconds_before_web_end` int(11) DEFAULT NULL,
  `seconds_before_ticketing_active` int(11) DEFAULT NULL,
  `permit_number` varchar(20) DEFAULT NULL,
  `permit_issue_date` date DEFAULT NULL,
  `permit_expiry_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_amenity`
--

CREATE TABLE `bus_amenity` (
  `bus_id` int(11) NOT NULL,
  `amenity_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_bus_route`
--

CREATE TABLE `bus_bus_route` (
  `id` int(11) NOT NULL,
  `bus_id` int(11) NOT NULL,
  `bus_route_id` int(11) NOT NULL,
  `load_factor` float NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_fare`
--

CREATE TABLE `bus_fare` (
  `id` int(11) NOT NULL,
  `bus_sector_id` int(11) NOT NULL,
  `travel_class_id` int(11) NOT NULL,
  `supplier_group_id` int(11) DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `bus_id` int(11) DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `currency_code` varchar(4) NOT NULL,
  `infant_fare` double NOT NULL,
  `child_fare` double NOT NULL,
  `adult_fare` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_held_item`
--

CREATE TABLE `bus_held_item` (
  `id` int(11) NOT NULL,
  `session_id` varchar(50) NOT NULL,
  `idx` int(11) NOT NULL,
  `result_idx` varchar(20) NOT NULL,
  `sector_idx` int(11) NOT NULL,
  `schedule_id` int(11) NOT NULL,
  `from_bus_stop_id` int(11) NOT NULL,
  `to_bus_stop_id` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_held_item_seat`
--

CREATE TABLE `bus_held_item_seat` (
  `bus_held_item_id` int(11) NOT NULL,
  `seat_number` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_image`
--

CREATE TABLE `bus_image` (
  `id` int(11) NOT NULL,
  `bus_id` int(11) NOT NULL,
  `image` mediumblob NOT NULL,
  `type` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Stand-in structure for view `bus_light`
-- (See below for the actual view)
--
CREATE TABLE `bus_light` (
`id` int(11)
,`plate_number` varchar(50)
,`name` varchar(50)
,`bus_type_id` int(11)
,`travel_class_id` int(11)
,`supplier_id` int(11)
,`division_id` int(11)
,`markup_only` tinyint(1)
,`cash_on_departure_allowed` tinyint(1)
,`notification_method` varchar(50)
,`allowed_divisions` decimal(50,0)
,`contact` varchar(50)
,`admin_contact` varchar(50)
,`driver_id` int(11)
,`conductor_id` int(11)
,`seating_profile_id` int(11)
,`rating` float
,`seconds_before_tb_end` int(11)
,`seconds_before_web_end` int(11)
,`seconds_before_ticketing_active` int(11)
,`permit_number` varchar(20)
,`permit_issue_date` date
,`permit_expiry_date` date
);

-- --------------------------------------------------------

--
-- Table structure for table `bus_mobile_location`
--

CREATE TABLE `bus_mobile_location` (
  `id` int(11) NOT NULL,
  `bus_id` int(11) NOT NULL,
  `location` point NOT NULL,
  `speed` float NOT NULL DEFAULT '0' COMMENT 'Speed in meters/second',
  `bearing` float DEFAULT NULL COMMENT 'Bearing of the device. Ranges from 0 - 360',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_route`
--

CREATE TABLE `bus_route` (
  `id` int(11) NOT NULL,
  `route_number` varchar(15) NOT NULL,
  `display_number` varchar(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `from_city_id` int(11) NOT NULL,
  `to_city_id` int(11) NOT NULL,
  `gender_required` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_route_bus_stop`
--

CREATE TABLE `bus_route_bus_stop` (
  `id` int(11) NOT NULL,
  `route_id` int(11) NOT NULL,
  `bus_stop_id` int(11) NOT NULL,
  `idx` int(11) NOT NULL,
  `waiting_time` time DEFAULT NULL,
  `travel_time` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Stand-in structure for view `bus_route_light`
-- (See below for the actual view)
--
CREATE TABLE `bus_route_light` (
`id` int(11)
,`route_number` varchar(15)
,`display_number` varchar(20)
,`name` varchar(50)
,`from_city_id` int(11)
,`to_city_id` int(11)
,`gender_required` tinyint(1)
);

-- --------------------------------------------------------

--
-- Table structure for table `bus_schedule`
--

CREATE TABLE `bus_schedule` (
  `id` int(11) NOT NULL,
  `bus_id` int(11) NOT NULL,
  `bus_route_id` int(11) NOT NULL,
  `terminal_in_time` datetime DEFAULT NULL,
  `departure_time` datetime NOT NULL,
  `arrival_time` datetime NOT NULL,
  `stage_id` int(11) NOT NULL DEFAULT '2',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `booking_allowed` tinyint(1) NOT NULL DEFAULT '1',
  `allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0',
  `driver_id` int(11) NOT NULL,
  `conductor_id` int(11) NOT NULL,
  `load_factor` float NOT NULL DEFAULT '0',
  `seating_profile_id` int(11) DEFAULT NULL,
  `tb_booking_end_time` timestamp NULL DEFAULT NULL,
  `web_booking_end_time` timestamp NULL DEFAULT NULL,
  `ticketing_active_time` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_schedule_bus_stop`
--

CREATE TABLE `bus_schedule_bus_stop` (
  `id` int(11) NOT NULL,
  `schedule_id` int(11) NOT NULL,
  `bus_stop_id` int(11) NOT NULL,
  `idx` int(11) NOT NULL,
  `arrival_time` datetime NOT NULL,
  `departure_time` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Stand-in structure for view `bus_schedule_light`
-- (See below for the actual view)
--
CREATE TABLE `bus_schedule_light` (
`id` int(11)
,`bus_id` int(11)
,`bus_route_id` int(11)
,`terminal_in_time` datetime
,`departure_time` datetime
,`arrival_time` datetime
,`stage_id` int(11)
,`active` tinyint(1)
,`booking_allowed` tinyint(1)
,`allowed_divisions` decimal(50,0)
,`driver_id` int(11)
,`conductor_id` int(11)
,`load_factor` float
,`seating_profile_id` int(11)
,`tb_booking_end_time` timestamp
,`web_booking_end_time` timestamp
,`ticketing_active_time` timestamp
,`bus_route_name` varchar(50)
,`bus_plate_number` varchar(50)
);

-- --------------------------------------------------------

--
-- Table structure for table `bus_seat`
--

CREATE TABLE `bus_seat` (
  `id` int(11) NOT NULL,
  `bus_type_id` int(11) NOT NULL,
  `seat_number` varchar(10) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `type` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_sector`
--

CREATE TABLE `bus_sector` (
  `id` int(11) NOT NULL,
  `bus_route_id` int(11) NOT NULL,
  `from_city_id` int(11) NOT NULL,
  `to_city_id` int(11) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_stop`
--

CREATE TABLE `bus_stop` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `name_si` varchar(100) NOT NULL,
  `name_ta` varchar(100) NOT NULL,
  `city_id` int(11) NOT NULL,
  `latitude` float DEFAULT NULL,
  `longitude` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_type`
--

CREATE TABLE `bus_type` (
  `id` int(11) NOT NULL,
  `type` varchar(40) NOT NULL,
  `description` text NOT NULL,
  `seating_capacity` int(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `cancellation_rule`
--

CREATE TABLE `cancellation_rule` (
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `change`
--

CREATE TABLE `change` (
  `id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `booking_item_id` int(11) DEFAULT NULL,
  `booking_item_passenger_id` int(11) DEFAULT NULL,
  `change_type_id` int(11) NOT NULL,
  `description` text,
  `user_id` int(11) DEFAULT NULL,
  `change_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Records a change in the booking or a booking item';

-- --------------------------------------------------------

--
-- Table structure for table `change_type`
--

CREATE TABLE `change_type` (
  `id` int(11) NOT NULL,
  `code` varchar(4) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `charge_rule`
--

CREATE TABLE `charge_rule` (
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `city`
--

CREATE TABLE `city` (
  `id` int(11) NOT NULL,
  `code` varchar(4) NOT NULL,
  `name` varchar(100) NOT NULL,
  `name_si` varchar(100) NOT NULL,
  `name_ta` varchar(100) NOT NULL,
  `district_id` int(11) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `client`
--

CREATE TABLE `client` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `mobile_telephone` varchar(20) DEFAULT NULL,
  `nic` varchar(12) DEFAULT NULL,
  `email` varchar(75) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `company`
--

CREATE TABLE `company` (
  `id` int(11) NOT NULL,
  `code` varchar(4) NOT NULL,
  `name` varchar(50) NOT NULL,
  `logo_file` varchar(50) DEFAULT NULL,
  `bitmask` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `conductor`
--

CREATE TABLE `conductor` (
  `id` int(11) NOT NULL,
  `person_id` int(11) NOT NULL,
  `nick_name` varchar(50) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0',
  `ntc_registration_number` varchar(20) DEFAULT NULL,
  `ntc_registration_expiry_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `config`
--

CREATE TABLE `config` (
  `config` varchar(100) NOT NULL,
  `value` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `country`
--

CREATE TABLE `country` (
  `id` int(11) NOT NULL,
  `code` varchar(4) NOT NULL,
  `name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `coupon`
--

CREATE TABLE `coupon` (
  `id` int(11) NOT NULL,
  `number` varchar(20) NOT NULL,
  `amount` double NOT NULL,
  `issue_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `expiry_date` date DEFAULT NULL,
  `client_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Stand-in structure for view `current_fares`
-- (See below for the actual view)
--
CREATE TABLE `current_fares` (
`bus_sector_id` int(11)
,`bus_route_id` int(11)
,`from_city_id` int(11)
,`to_city_id` int(11)
,`fare_id` int(11)
,`travel_class_id` int(11)
,`adult_fare` double
);

-- --------------------------------------------------------

--
-- Table structure for table `discount_code`
--

CREATE TABLE `discount_code` (
  `id` int(11) NOT NULL,
  `code` varchar(10) NOT NULL,
  `scheme_code` varchar(10) NOT NULL,
  `active` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `discount_rule`
--

CREATE TABLE `discount_rule` (
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `district`
--

CREATE TABLE `district` (
  `id` int(11) NOT NULL,
  `code` varchar(4) NOT NULL,
  `name` varchar(30) NOT NULL,
  `province_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `division`
--

CREATE TABLE `division` (
  `id` int(11) NOT NULL,
  `code` varchar(4) NOT NULL,
  `name` varchar(50) NOT NULL,
  `company_id` int(11) NOT NULL,
  `bitmask` decimal(50,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `driver`
--

CREATE TABLE `driver` (
  `id` int(11) NOT NULL,
  `person_id` int(11) NOT NULL,
  `nick_name` varchar(50) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0',
  `driving_licence` varchar(10) DEFAULT NULL,
  `driving_licence_issue_date` date DEFAULT NULL,
  `driving_licence_expiry_date` date DEFAULT NULL,
  `ntc_registration_number` varchar(20) DEFAULT NULL,
  `ntc_registration_expiry_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Stand-in structure for view `gross_profit`
-- (See below for the actual view)
--
CREATE TABLE `gross_profit` (
`booking_month` varchar(16)
,`gross_profit` double(17,0)
);

-- --------------------------------------------------------

--
-- Table structure for table `markup_rule`
--

CREATE TABLE `markup_rule` (
  `id` int(11) NOT NULL,
  `is_margin` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `mobile_device`
--

CREATE TABLE `mobile_device` (
  `id` int(11) NOT NULL,
  `mobile_number` varchar(10) NOT NULL,
  `imei` varchar(16) NOT NULL,
  `bus_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `module`
--

CREATE TABLE `module` (
  `id` int(11) NOT NULL,
  `code` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Stand-in structure for view `operational_schedule`
-- (See below for the actual view)
--
CREATE TABLE `operational_schedule` (
`schedule_id` int(11)
,`terminal_in` datetime
,`terminal_out` datetime
,`stage` varchar(10)
,`route_id` int(11)
,`from_city_id` int(11)
,`route_name` varchar(50)
,`route_number` varchar(15)
,`route_number_name` varchar(71)
,`plate_number` varchar(50)
,`conductor_name` varchar(101)
,`conductor_mobile` varchar(20)
,`driver_name` varchar(101)
,`driver_mobile` varchar(20)
,`bus_mobile` varchar(50)
,`seat_reservations` bigint(21)
,`total_cost` double
,`seating_profile_id` int(11)
,`division_id` int(11)
,`allowed_divisions` decimal(50,0)
);

-- --------------------------------------------------------

--
-- Table structure for table `operational_stage`
--

CREATE TABLE `operational_stage` (
  `id` int(11) NOT NULL,
  `ordinal` int(11) NOT NULL,
  `code` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `passenger`
--

CREATE TABLE `passenger` (
  `id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `idx` int(11) NOT NULL,
  `passenger_type` varchar(20) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `nic` varchar(12) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `email` varchar(75) DEFAULT NULL,
  `mobile_telephone` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `password_reset_token`
--

CREATE TABLE `password_reset_token` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `token` varchar(128) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0',
  `write_allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `payment_refund`
--

CREATE TABLE `payment_refund` (
  `id` int(11) NOT NULL,
  `type` varchar(10) NOT NULL,
  `amount` double NOT NULL COMMENT 'in LKR',
  `actual_amount` double NOT NULL,
  `actual_currency` varchar(10) NOT NULL,
  `mode` varchar(20) NOT NULL,
  `vendor_mode` varchar(20) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` int(11) DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `permission`
--

CREATE TABLE `permission` (
  `id` int(11) NOT NULL,
  `type` varchar(25) NOT NULL,
  `code` varchar(100) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` text NOT NULL,
  `module_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `permission_group_permission`
--

CREATE TABLE `permission_group_permission` (
  `permission_group_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `person`
--

CREATE TABLE `person` (
  `id` int(11) NOT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `middle_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `nic` varchar(12) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `mobile_telephone` varchar(20) DEFAULT NULL,
  `home_telephone` varchar(20) DEFAULT NULL,
  `work_telephone` varchar(20) DEFAULT NULL,
  `email` varchar(75) DEFAULT NULL,
  `house_number` varchar(100) DEFAULT NULL,
  `street` varchar(100) DEFAULT NULL,
  `city` varchar(30) DEFAULT NULL,
  `postal_code` int(5) DEFAULT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `province`
--

CREATE TABLE `province` (
  `id` int(11) NOT NULL,
  `code` varchar(4) NOT NULL,
  `name` varchar(30) NOT NULL,
  `country_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `refund`
--

CREATE TABLE `refund` (
  `id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0',
  `write_allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `report_param`
--

CREATE TABLE `report_param` (
  `id` int(10) NOT NULL,
  `report_id` int(10) NOT NULL,
  `name` varchar(20) NOT NULL,
  `type` varchar(20) NOT NULL,
  `visible` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `report_type`
--

CREATE TABLE `report_type` (
  `id` int(10) NOT NULL,
  `report_name` varchar(20) NOT NULL,
  `report_type` varchar(20) NOT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Stand-in structure for view `revenue`
-- (See below for the actual view)
--
CREATE TABLE `revenue` (
`booking_month` varchar(16)
,`accountable_revenue` double(17,0)
,`total_revenue` double(17,0)
,`accountable_bookings` decimal(23,0)
,`total_bookings` bigint(21)
,`busbooking.lk_bookings` decimal(23,0)
,`superline.lk_bookings` decimal(23,0)
,`BBK_bookings` decimal(23,0)
,`TB_bookings` decimal(23,0)
);

-- --------------------------------------------------------

--
-- Table structure for table `REVINFO`
--

CREATE TABLE `REVINFO` (
  `REV` int(11) NOT NULL,
  `REVTSTMP` bigint(20) DEFAULT NULL,
  `USERNAME` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `rule`
--

CREATE TABLE `rule` (
  `id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `description` text NOT NULL,
  `salience` float NOT NULL,
  `scheme` varchar(50) NOT NULL,
  `rule_condition` int(11) DEFAULT NULL,
  `amount` float NOT NULL,
  `application_type` varchar(25) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `rule_condition`
--

CREATE TABLE `rule_condition` (
  `id` int(11) NOT NULL,
  `type` varchar(25) NOT NULL,
  `property` varchar(25) DEFAULT NULL,
  `qualifier` varchar(25) DEFAULT NULL,
  `value_type` varchar(50) DEFAULT NULL,
  `value_string` varchar(50) DEFAULT NULL,
  `first_rule` int(11) DEFAULT NULL,
  `combiner` varchar(10) DEFAULT NULL,
  `second_rule` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `seating_profile`
--

CREATE TABLE `seating_profile` (
  `id` int(11) NOT NULL,
  `bus_type_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `seating_profile_seat`
--

CREATE TABLE `seating_profile_seat` (
  `seating_profile_id` int(11) NOT NULL,
  `seat_number` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `service`
--

CREATE TABLE `service` (
  `id` int(11) NOT NULL,
  `route_id` int(11) NOT NULL,
  `monday` tinyint(1) NOT NULL,
  `tuesday` tinyint(1) NOT NULL,
  `wednesday` tinyint(1) NOT NULL,
  `thursday` tinyint(1) NOT NULL,
  `friday` tinyint(1) NOT NULL,
  `saturday` tinyint(1) NOT NULL,
  `sunday` tinyint(1) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `departure_time` time DEFAULT NULL COMMENT 'Departure time of a time based service',
  `start_time` time DEFAULT NULL COMMENT 'Start time of a time slab of a frequency based service',
  `end_time` time DEFAULT NULL COMMENT 'End time of a time slab of a frequency based service',
  `frequency` int(11) DEFAULT NULL COMMENT 'In seconds',
  `exact_times` tinyint(1) DEFAULT NULL COMMENT 'Whether to generate exact times based on frequencies'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `staff_number` varchar(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `supplier`
--

CREATE TABLE `supplier` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0',
  `supplier_group_id` int(11) DEFAULT NULL,
  `preferred_payment_mode` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `supplier_account`
--

CREATE TABLE `supplier_account` (
  `id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `number` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `bank` varchar(100) NOT NULL,
  `branch` varchar(50) NOT NULL,
  `is_primary` bit(1) NOT NULL DEFAULT b'1',
  `allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0',
  `swift` varchar(20) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `supplier_contact_person`
--

CREATE TABLE `supplier_contact_person` (
  `id` int(11) NOT NULL,
  `person_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `is_primary` bit(1) NOT NULL,
  `is_owner` bit(1) NOT NULL,
  `allowed_divisions` decimal(50,0) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `supplier_group`
--

CREATE TABLE `supplier_group` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `supplier_payment`
--

CREATE TABLE `supplier_payment` (
  `id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `amount` double NOT NULL,
  `currency` varchar(10) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `mode` varchar(10) NOT NULL,
  `reference` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `tax_rule`
--

CREATE TABLE `tax_rule` (
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `travel_class`
--

CREATE TABLE `travel_class` (
  `id` int(11) NOT NULL,
  `code` varchar(4) NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(60) NOT NULL,
  `account_status_code` varchar(4) NOT NULL,
  `division_id` int(11) NOT NULL,
  `accountable` tinyint(1) NOT NULL,
  `agent_id` int(11) DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `middle_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `nic` varchar(12) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `mobile_telephone` varchar(20) DEFAULT NULL,
  `home_telephone` varchar(20) DEFAULT NULL,
  `work_telephone` varchar(20) DEFAULT NULL,
  `email` varchar(75) DEFAULT NULL,
  `house_number` varchar(100) DEFAULT NULL,
  `street` varchar(100) DEFAULT NULL,
  `city` varchar(30) DEFAULT NULL,
  `postal_code` int(5) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user_group`
--

CREATE TABLE `user_group` (
  `id` int(11) NOT NULL,
  `code` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` text NOT NULL,
  `division_id` int(11) DEFAULT NULL,
  `agent_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user_group_division`
--

CREATE TABLE `user_group_division` (
  `user_group_id` int(11) NOT NULL,
  `division_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user_group_permission`
--

CREATE TABLE `user_group_permission` (
  `user_group_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Stand-in structure for view `user_light`
-- (See below for the actual view)
--
CREATE TABLE `user_light` (
`id` int(11)
,`username` varchar(50)
,`password` varchar(60)
,`account_status_code` varchar(4)
,`division_id` int(11)
,`accountable` tinyint(1)
,`agent_id` int(11)
,`first_name` varchar(50)
,`middle_name` varchar(50)
,`last_name` varchar(50)
,`nic` varchar(12)
,`gender` varchar(10)
,`dob` date
,`mobile_telephone` varchar(20)
,`home_telephone` varchar(20)
,`work_telephone` varchar(20)
,`email` varchar(75)
,`house_number` varchar(100)
,`street` varchar(100)
,`city` varchar(30)
,`postal_code` int(5)
);

-- --------------------------------------------------------

--
-- Table structure for table `user_login`
--

CREATE TABLE `user_login` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `time` datetime NOT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `user_agent` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user_user_group`
--

CREATE TABLE `user_user_group` (
  `user_id` int(11) NOT NULL,
  `user_group_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure for view `booking_light`
--
DROP TABLE IF EXISTS `booking_light`;

CREATE ALGORITHM=UNDEFINED DEFINER=`bbkservicesadmin`@`%` SQL SECURITY DEFINER VIEW `booking_light`  AS  select `booking`.`id` AS `id`,`booking`.`reference` AS `reference`,`booking`.`booking_time` AS `booking_time`,`booking`.`expiry_time` AS `expiry_time`,`booking`.`chargeable` AS `chargeable`,`booking`.`status_code` AS `status_code`,`booking`.`cancellation_cause` AS `cancellation_cause`,`booking`.`client_id` AS `client_id`,`booking`.`agent_id` AS `agent_id`,`booking`.`user_id` AS `user_id`,`booking`.`remarks` AS `remarks`,`booking`.`division_id` AS `division_id`,`booking`.`allowed_divisions` AS `allowed_divisions`,`booking`.`write_allowed_divisions` AS `write_allowed_divisions`,`client`.`nic` AS `client_nic`,`client`.`mobile_telephone` AS `client_mobile_telephone`,`client`.`name` AS `client_name`,`city`.`name` AS `from_city`,`c`.`name` AS `to_city` from ((((((`booking` join `client` on((`client`.`id` = `booking`.`client_id`))) join `booking_item` on((`booking_item`.`booking_id` = `booking`.`id`))) join `bus_stop` on((`booking_item`.`from_bus_stop_id` = `bus_stop`.`id`))) join `city` on((`bus_stop`.`city_id` = `city`.`id`))) join `bus_stop` `bs` on((`booking_item`.`to_bus_stop_id` = `bs`.`id`))) join `city` `c` on((`bs`.`city_id` = `c`.`id`))) order by `booking`.`id` ;

-- --------------------------------------------------------

--
-- Structure for view `booking_payments`
--
DROP TABLE IF EXISTS `booking_payments`;

CREATE ALGORITHM=UNDEFINED DEFINER=`bbkservicesadmin`@`%` SQL SECURITY DEFINER VIEW `booking_payments`  AS  select `booking`.`id` AS `id`,`booking`.`reference` AS `reference`,`booking`.`booking_time` AS `booking_time`,`booking`.`expiry_time` AS `expiry_time`,`booking`.`chargeable` AS `chargeable`,`booking`.`status_code` AS `status_code`,`booking`.`cancellation_cause` AS `cancellation_cause`,`booking`.`client_id` AS `client_id`,`booking`.`agent_id` AS `agent_id`,`booking`.`user_id` AS `user_id`,`booking`.`remarks` AS `remarks`,`booking`.`allowed_divisions` AS `allowed_divisions`,sum(`pr1`.`amount`) AS `total_payments`,group_concat(`pr1`.`mode` separator ',') AS `payment_modes`,sum(`pr2`.`amount`) AS `total_refunds` from ((`booking` left join (`payment` join `payment_refund` `pr1` on((`payment`.`id` = `pr1`.`id`))) on((`booking`.`id` = `payment`.`booking_id`))) left join (`refund` join `payment_refund` `pr2` on((`refund`.`id` = `pr2`.`id`))) on((`booking`.`id` = `refund`.`booking_id`))) group by `booking`.`id` order by `booking`.`id` ;

-- --------------------------------------------------------

--
-- Structure for view `bus_light`
--
DROP TABLE IF EXISTS `bus_light`;

CREATE ALGORITHM=UNDEFINED DEFINER=`bbkservicesadmin`@`%` SQL SECURITY DEFINER VIEW `bus_light`  AS  select `bus`.`id` AS `id`,`bus`.`plate_number` AS `plate_number`,`bus`.`name` AS `name`,`bus`.`bus_type_id` AS `bus_type_id`,`bus`.`travel_class_id` AS `travel_class_id`,`bus`.`supplier_id` AS `supplier_id`,`bus`.`division_id` AS `division_id`,`bus`.`markup_only` AS `markup_only`,`bus`.`cash_on_departure_allowed` AS `cash_on_departure_allowed`,`bus`.`notification_method` AS `notification_method`,`bus`.`allowed_divisions` AS `allowed_divisions`,`bus`.`contact` AS `contact`,`bus`.`admin_contact` AS `admin_contact`,`bus`.`driver_id` AS `driver_id`,`bus`.`conductor_id` AS `conductor_id`,`bus`.`seating_profile_id` AS `seating_profile_id`,`bus`.`rating` AS `rating`,`bus`.`seconds_before_tb_end` AS `seconds_before_tb_end`,`bus`.`seconds_before_web_end` AS `seconds_before_web_end`,`bus`.`seconds_before_ticketing_active` AS `seconds_before_ticketing_active`,`bus`.`permit_number` AS `permit_number`,`bus`.`permit_issue_date` AS `permit_issue_date`,`bus`.`permit_expiry_date` AS `permit_expiry_date` from `bus` ;

-- --------------------------------------------------------

--
-- Structure for view `bus_route_light`
--
DROP TABLE IF EXISTS `bus_route_light`;

CREATE ALGORITHM=UNDEFINED DEFINER=`bbkservicesadmin`@`%` SQL SECURITY DEFINER VIEW `bus_route_light`  AS  select `bus_route`.`id` AS `id`,`bus_route`.`route_number` AS `route_number`,`bus_route`.`display_number` AS `display_number`,`bus_route`.`name` AS `name`,`bus_route`.`from_city_id` AS `from_city_id`,`bus_route`.`to_city_id` AS `to_city_id`,`bus_route`.`gender_required` AS `gender_required` from `bus_route` ;

-- --------------------------------------------------------

--
-- Structure for view `bus_schedule_light`
--
DROP TABLE IF EXISTS `bus_schedule_light`;

CREATE ALGORITHM=UNDEFINED DEFINER=`bbkservicesadmin`@`%` SQL SECURITY DEFINER VIEW `bus_schedule_light`  AS  select `bus_schedule`.`id` AS `id`,`bus_schedule`.`bus_id` AS `bus_id`,`bus_schedule`.`bus_route_id` AS `bus_route_id`,`bus_schedule`.`terminal_in_time` AS `terminal_in_time`,`bus_schedule`.`departure_time` AS `departure_time`,`bus_schedule`.`arrival_time` AS `arrival_time`,`bus_schedule`.`stage_id` AS `stage_id`,`bus_schedule`.`active` AS `active`,`bus_schedule`.`booking_allowed` AS `booking_allowed`,`bus_schedule`.`allowed_divisions` AS `allowed_divisions`,`bus_schedule`.`driver_id` AS `driver_id`,`bus_schedule`.`conductor_id` AS `conductor_id`,`bus_schedule`.`load_factor` AS `load_factor`,`bus_schedule`.`seating_profile_id` AS `seating_profile_id`,`bus_schedule`.`tb_booking_end_time` AS `tb_booking_end_time`,`bus_schedule`.`web_booking_end_time` AS `web_booking_end_time`,`bus_schedule`.`ticketing_active_time` AS `ticketing_active_time`,`bus_route`.`name` AS `bus_route_name`,`bus`.`plate_number` AS `bus_plate_number` from ((`bus_schedule` join `bus_route` on((`bus_route`.`id` = `bus_schedule`.`bus_route_id`))) join `bus` on((`bus`.`id` = `bus_schedule`.`bus_id`))) ;

-- --------------------------------------------------------

--
-- Structure for view `current_fares`
--
DROP TABLE IF EXISTS `current_fares`;

CREATE ALGORITHM=UNDEFINED DEFINER=`bbkservicesadmin`@`%` SQL SECURITY DEFINER VIEW `current_fares`  AS  select `bus_sector`.`id` AS `bus_sector_id`,`bus_sector`.`bus_route_id` AS `bus_route_id`,`bus_sector`.`from_city_id` AS `from_city_id`,`bus_sector`.`to_city_id` AS `to_city_id`,`bus_fare`.`id` AS `fare_id`,`bus_fare`.`travel_class_id` AS `travel_class_id`,`bus_fare`.`adult_fare` AS `adult_fare` from (`bus_sector` join `bus_fare` on((`bus_sector`.`id` = `bus_fare`.`bus_sector_id`))) where isnull(`bus_fare`.`end_time`) ;

-- --------------------------------------------------------

--
-- Structure for view `gross_profit`
--
DROP TABLE IF EXISTS `gross_profit`;

CREATE ALGORITHM=UNDEFINED DEFINER=`bbkservicesadmin`@`%` SQL SECURITY DEFINER VIEW `gross_profit`  AS  select concat(year(`booking`.`booking_time`),' - ',convert(monthname(`booking`.`booking_time`) using utf8mb4)) AS `booking_month`,round(sum(`booking_item_markup`.`amount`),0) AS `gross_profit` from ((`booking` join `booking_item` on((`booking_item`.`booking_id` = `booking`.`id`))) join `booking_item_markup` on((`booking_item_markup`.`booking_item_id` = `booking_item`.`id`))) where ((`booking`.`chargeable` > 100) and (`booking`.`status_code` = 'CONF') and (`booking`.`booking_time` > '2015-01-01 00:00:00')) group by `booking_month` order by year(`booking`.`booking_time`),month(`booking`.`booking_time`) ;

-- --------------------------------------------------------

--
-- Structure for view `operational_schedule`
--
DROP TABLE IF EXISTS `operational_schedule`;

CREATE ALGORITHM=UNDEFINED DEFINER=`bbkservicesadmin`@`%` SQL SECURITY DEFINER VIEW `operational_schedule`  AS  select `bus_schedule`.`id` AS `schedule_id`,`bus_schedule`.`terminal_in_time` AS `terminal_in`,`bus_schedule`.`departure_time` AS `terminal_out`,`operational_stage`.`code` AS `stage`,`bus_route`.`id` AS `route_id`,`bus_route`.`from_city_id` AS `from_city_id`,`bus_route`.`name` AS `route_name`,`bus_route`.`route_number` AS `route_number`,concat(`bus_route`.`display_number`,' ',`bus_route`.`name`) AS `route_number_name`,`bus`.`plate_number` AS `plate_number`,concat(`conductor_person`.`first_name`,' ',`conductor_person`.`last_name`) AS `conductor_name`,`conductor_person`.`mobile_telephone` AS `conductor_mobile`,concat(`driver_person`.`first_name`,' ',`driver_person`.`last_name`) AS `driver_name`,`driver_person`.`mobile_telephone` AS `driver_mobile`,`bus`.`contact` AS `bus_mobile`,count(0) AS `seat_reservations`,sum(`booking_item`.`cost`) AS `total_cost`,`bus_schedule`.`seating_profile_id` AS `seating_profile_id`,`bus`.`division_id` AS `division_id`,`bus_schedule`.`allowed_divisions` AS `allowed_divisions` from (((((((((`bus_schedule` join `bus` on((`bus_schedule`.`bus_id` = `bus`.`id`))) join `bus_route` on((`bus_schedule`.`bus_route_id` = `bus_route`.`id`))) join `operational_stage` on((`bus_schedule`.`stage_id` = `operational_stage`.`id`))) join `conductor` on((`bus_schedule`.`conductor_id` = `conductor`.`id`))) join `person` `conductor_person` on((`conductor`.`person_id` = `conductor_person`.`id`))) join `driver` on((`bus_schedule`.`driver_id` = `driver`.`id`))) join `person` `driver_person` on((`driver`.`person_id` = `driver_person`.`id`))) join `booking_item` on((`bus_schedule`.`id` = `booking_item`.`schedule_id`))) join `booking_item_passenger` on((`booking_item`.`id` = `booking_item_passenger`.`booking_item_id`))) where ((`bus_schedule`.`active` = '1') and (`booking_item_passenger`.`status_code` = 'CONF')) group by `booking_item`.`schedule_id` order by `bus_schedule`.`departure_time` ;

-- --------------------------------------------------------

--
-- Structure for view `revenue`
--
DROP TABLE IF EXISTS `revenue`;

CREATE ALGORITHM=UNDEFINED DEFINER=`bbkservicesadmin`@`%` SQL SECURITY DEFINER VIEW `revenue`  AS  select concat(year(`booking`.`booking_time`),' - ',convert(monthname(`booking`.`booking_time`) using utf8mb4)) AS `booking_month`,round(sum(if((`user`.`accountable` = 1),`booking`.`chargeable`,0)),0) AS `accountable_revenue`,round(sum(`booking`.`chargeable`),0) AS `total_revenue`,sum(if((`user`.`accountable` = 1),1,0)) AS `accountable_bookings`,count(0) AS `total_bookings`,sum(if((`user`.`id` = 156),1,0)) AS `busbooking.lk_bookings`,sum(if((`user`.`id` = 201),1,0)) AS `superline.lk_bookings`,sum(if((`user`.`division_id` = 1),1,0)) AS `BBK_bookings`,sum(if(((`user`.`id` <> 156) and (`user`.`id` <> 201) and (`user`.`id` <> 2036)),1,0)) AS `TB_bookings` from (`booking` join `user` on((`user`.`id` = `booking`.`user_id`))) where ((`booking`.`chargeable` > 100) and (`booking`.`status_code` = 'CONF') and (`booking`.`booking_time` > '2015-01-01 00:00:00')) group by `booking_month` order by year(`booking`.`booking_time`),month(`booking`.`booking_time`) ;

-- --------------------------------------------------------

--
-- Structure for view `user_light`
--
DROP TABLE IF EXISTS `user_light`;

CREATE ALGORITHM=UNDEFINED DEFINER=`bbkservicesadmin`@`%` SQL SECURITY DEFINER VIEW `user_light`  AS  select `user`.`id` AS `id`,`user`.`username` AS `username`,`user`.`password` AS `password`,`user`.`account_status_code` AS `account_status_code`,`user`.`division_id` AS `division_id`,`user`.`accountable` AS `accountable`,`user`.`agent_id` AS `agent_id`,`user`.`first_name` AS `first_name`,`user`.`middle_name` AS `middle_name`,`user`.`last_name` AS `last_name`,`user`.`nic` AS `nic`,`user`.`gender` AS `gender`,`user`.`dob` AS `dob`,`user`.`mobile_telephone` AS `mobile_telephone`,`user`.`home_telephone` AS `home_telephone`,`user`.`work_telephone` AS `work_telephone`,`user`.`email` AS `email`,`user`.`house_number` AS `house_number`,`user`.`street` AS `street`,`user`.`city` AS `city`,`user`.`postal_code` AS `postal_code` from `user` ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account_status`
--
ALTER TABLE `account_status`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Indexes for table `agent`
--
ALTER TABLE `agent`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `agent_number` (`name`),
  ADD KEY `allowed_divisions` (`allowed_divisions`);

--
-- Indexes for table `agent_allocation`
--
ALTER TABLE `agent_allocation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bus_route_id` (`bus_route_id`),
  ADD KEY `agent_id` (`agent_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`);

--
-- Indexes for table `agent_allocation_seat`
--
ALTER TABLE `agent_allocation_seat`
  ADD UNIQUE KEY `agent_allocation_id` (`agent_allocation_id`,`seat_number`);

--
-- Indexes for table `agent_restriction`
--
ALTER TABLE `agent_restriction`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `agent_id_2` (`agent_id`,`bus_id`),
  ADD KEY `agent_id` (`agent_id`),
  ADD KEY `bus_id` (`bus_id`),
  ADD KEY `type` (`type`);

--
-- Indexes for table `amenity`
--
ALTER TABLE `amenity`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Indexes for table `api_token`
--
ALTER TABLE `api_token`
  ADD PRIMARY KEY (`id`),
  ADD KEY `username` (`username`);

--
-- Indexes for table `aud__booking`
--
ALTER TABLE `aud__booking`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK142961AADF74E053` (`REV`);

--
-- Indexes for table `aud__booking_item`
--
ALTER TABLE `aud__booking_item`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FKA72F648ADF74E053` (`REV`);

--
-- Indexes for table `aud__booking_item_cancellation`
--
ALTER TABLE `aud__booking_item_cancellation`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK36CF1D9ADF74E053` (`REV`);

--
-- Indexes for table `aud__booking_item_charge`
--
ALTER TABLE `aud__booking_item_charge`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FKA1D6F8F6DF74E054` (`REV`);

--
-- Indexes for table `aud__booking_item_discount`
--
ALTER TABLE `aud__booking_item_discount`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK36B1CDD8DF74E053` (`REV`);

--
-- Indexes for table `aud__booking_item_markup`
--
ALTER TABLE `aud__booking_item_markup`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK2F5D3CBFDF74E053` (`REV`);

--
-- Indexes for table `aud__booking_item_passenger`
--
ALTER TABLE `aud__booking_item_passenger`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FKA8165AE5DF74E053` (`REV`);

--
-- Indexes for table `aud__booking_item_tax`
--
ALTER TABLE `aud__booking_item_tax`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FKA1D6F8F6DF74E053` (`REV`);

--
-- Indexes for table `aud__bus`
--
ALTER TABLE `aud__bus`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK_fpo3en59wah2q0j6tvytt4byw` (`REV`);

--
-- Indexes for table `aud__bus_amenity`
--
ALTER TABLE `aud__bus_amenity`
  ADD PRIMARY KEY (`REV`,`bus_id`,`amenity_id`);

--
-- Indexes for table `aud__bus_bus_route`
--
ALTER TABLE `aud__bus_bus_route`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK_pyvx6na254wodm0ykr0q3cr55` (`REV`);

--
-- Indexes for table `aud__change`
--
ALTER TABLE `aud__change`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK80F1E981DF74E053` (`REV`);

--
-- Indexes for table `aud__conductor`
--
ALTER TABLE `aud__conductor`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK_kwyflw9mwoatb0im2sjwtymit` (`REV`);

--
-- Indexes for table `aud__driver`
--
ALTER TABLE `aud__driver`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK_bmjha3uk1cofprvsh3gffu6y` (`REV`);

--
-- Indexes for table `aud__passenger`
--
ALTER TABLE `aud__passenger`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK8BA56BDF74E053` (`REV`);

--
-- Indexes for table `aud__payment`
--
ALTER TABLE `aud__payment`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FKA433ECD71D79195C` (`id`,`REV`);

--
-- Indexes for table `aud__payment_refund`
--
ALTER TABLE `aud__payment_refund`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK60C05522DF74E053` (`REV`);

--
-- Indexes for table `aud__person`
--
ALTER TABLE `aud__person`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK_hvhc8n3pslqac4afkiq58y1k7` (`REV`);

--
-- Indexes for table `aud__refund`
--
ALTER TABLE `aud__refund`
  ADD PRIMARY KEY (`id`,`REV`),
  ADD KEY `FK9A0791491D79195C` (`id`,`REV`);

--
-- Indexes for table `booking`
--
ALTER TABLE `booking`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `reference` (`reference`),
  ADD KEY `status_code` (`status_code`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`),
  ADD KEY `client_id` (`client_id`),
  ADD KEY `agent_id` (`agent_id`),
  ADD KEY `division_id` (`division_id`),
  ADD KEY `write_allowed_divisions` (`write_allowed_divisions`);

--
-- Indexes for table `booking_item`
--
ALTER TABLE `booking_item`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `booking_id` (`booking_id`,`idx`),
  ADD KEY `status_code` (`status_code`),
  ADD KEY `schedule_id` (`schedule_id`),
  ADD KEY `from_bus_stop_id` (`from_bus_stop_id`),
  ADD KEY `to_bus_stop_id` (`to_bus_stop_id`),
  ADD KEY `booking_item_ibfk_3` (`schedule_id`,`from_bus_stop_id`),
  ADD KEY `booking_item_ibfk_4` (`schedule_id`,`to_bus_stop_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`),
  ADD KEY `write_allowed_divisions` (`write_allowed_divisions`);

--
-- Indexes for table `booking_item_cancellation`
--
ALTER TABLE `booking_item_cancellation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `booking_item_id` (`booking_item_id`),
  ADD KEY `cancellation_scheme_id` (`cancellation_scheme_id`);

--
-- Indexes for table `booking_item_charge`
--
ALTER TABLE `booking_item_charge`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `booking_item_id` (`booking_item_id`,`charge_scheme_id`),
  ADD KEY `charge_scheme_id` (`charge_scheme_id`);

--
-- Indexes for table `booking_item_discount`
--
ALTER TABLE `booking_item_discount`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `booking_item_id` (`booking_item_id`,`discount_scheme_id`),
  ADD KEY `discount_scheme_id` (`discount_scheme_id`);

--
-- Indexes for table `booking_item_markup`
--
ALTER TABLE `booking_item_markup`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `booking_item_id` (`booking_item_id`,`markup_scheme_id`),
  ADD KEY `markup_scheme_id` (`markup_scheme_id`);

--
-- Indexes for table `booking_item_passenger`
--
ALTER TABLE `booking_item_passenger`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `booking_item_id_passenger_id` (`booking_item_id`,`passenger_id`),
  ADD KEY `booking_item_id` (`booking_item_id`),
  ADD KEY `passenger_id` (`passenger_id`),
  ADD KEY `status_code` (`status_code`);

--
-- Indexes for table `booking_item_tax`
--
ALTER TABLE `booking_item_tax`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `booking_item_id` (`booking_item_id`,`tax_scheme_id`),
  ADD KEY `tax_scheme_id` (`tax_scheme_id`);

--
-- Indexes for table `booking_status`
--
ALTER TABLE `booking_status`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Indexes for table `bus`
--
ALTER TABLE `bus`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `plate_number` (`plate_number`),
  ADD KEY `bus_type_id` (`bus_type_id`),
  ADD KEY `supplier_id` (`supplier_id`),
  ADD KEY `driver_id` (`driver_id`),
  ADD KEY `conductor_id` (`conductor_id`),
  ADD KEY `travel_class_id` (`travel_class_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`),
  ADD KEY `division_id` (`division_id`),
  ADD KEY `seating_profile_id` (`seating_profile_id`);

--
-- Indexes for table `bus_amenity`
--
ALTER TABLE `bus_amenity`
  ADD PRIMARY KEY (`bus_id`,`amenity_id`),
  ADD KEY `amenity_id` (`amenity_id`);

--
-- Indexes for table `bus_bus_route`
--
ALTER TABLE `bus_bus_route`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bus_route_id` (`bus_route_id`),
  ADD KEY `bus_id` (`bus_id`);

--
-- Indexes for table `bus_fare`
--
ALTER TABLE `bus_fare`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bus_route_id` (`bus_sector_id`),
  ADD KEY `supplier_group_id` (`supplier_group_id`),
  ADD KEY `supplier_id` (`supplier_id`),
  ADD KEY `bus_id` (`bus_id`),
  ADD KEY `travel_class_id` (`travel_class_id`),
  ADD KEY `end_time` (`end_time`);

--
-- Indexes for table `bus_held_item`
--
ALTER TABLE `bus_held_item`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `session_id` (`session_id`,`idx`),
  ADD KEY `schedule_id` (`schedule_id`),
  ADD KEY `from_bus_stop_id` (`from_bus_stop_id`),
  ADD KEY `to_bus_stop_id` (`to_bus_stop_id`),
  ADD KEY `bus_held_item_ibfk_1` (`schedule_id`,`from_bus_stop_id`),
  ADD KEY `bus_held_item_ibfk_2` (`schedule_id`,`to_bus_stop_id`);

--
-- Indexes for table `bus_held_item_seat`
--
ALTER TABLE `bus_held_item_seat`
  ADD UNIQUE KEY `bus_held_item_id` (`bus_held_item_id`,`seat_number`);

--
-- Indexes for table `bus_image`
--
ALTER TABLE `bus_image`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bus_id` (`bus_id`);

--
-- Indexes for table `bus_mobile_location`
--
ALTER TABLE `bus_mobile_location`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bus_id` (`bus_id`);

--
-- Indexes for table `bus_route`
--
ALTER TABLE `bus_route`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `route_number` (`route_number`),
  ADD KEY `from_city` (`from_city_id`),
  ADD KEY `to_city` (`to_city_id`);

--
-- Indexes for table `bus_route_bus_stop`
--
ALTER TABLE `bus_route_bus_stop`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `route_id` (`route_id`,`bus_stop_id`,`idx`),
  ADD KEY `fk_routeBusStop_busStop` (`bus_stop_id`);

--
-- Indexes for table `bus_schedule`
--
ALTER TABLE `bus_schedule`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bus_id` (`bus_id`),
  ADD KEY `route_id` (`bus_route_id`),
  ADD KEY `driver_id` (`driver_id`),
  ADD KEY `conductor_id` (`conductor_id`),
  ADD KEY `seating_profile_id` (`seating_profile_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`),
  ADD KEY `departure_time` (`departure_time`),
  ADD KEY `stage_id` (`stage_id`),
  ADD KEY `composite` (`bus_route_id`,`departure_time`,`allowed_divisions`) USING BTREE;

--
-- Indexes for table `bus_schedule_bus_stop`
--
ALTER TABLE `bus_schedule_bus_stop`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `schedule_id_2` (`schedule_id`,`idx`),
  ADD KEY `schedule_id` (`schedule_id`,`bus_stop_id`),
  ADD KEY `bus_stop_id` (`bus_stop_id`);

--
-- Indexes for table `bus_seat`
--
ALTER TABLE `bus_seat`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_seat_no` (`bus_type_id`,`seat_number`),
  ADD UNIQUE KEY `bus_type_id` (`bus_type_id`,`x`,`y`);

--
-- Indexes for table `bus_sector`
--
ALTER TABLE `bus_sector`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bus_route_id` (`bus_route_id`),
  ADD KEY `from_city_id` (`from_city_id`),
  ADD KEY `to_city_id` (`to_city_id`);

--
-- Indexes for table `bus_stop`
--
ALTER TABLE `bus_stop`
  ADD PRIMARY KEY (`id`),
  ADD KEY `city_code` (`city_id`);

--
-- Indexes for table `bus_type`
--
ALTER TABLE `bus_type`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `cancellation_rule`
--
ALTER TABLE `cancellation_rule`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `change`
--
ALTER TABLE `change`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `change_type` (`change_type_id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `item_id` (`booking_item_id`),
  ADD KEY `booking_item_passenger_id` (`booking_item_passenger_id`);

--
-- Indexes for table `change_type`
--
ALTER TABLE `change_type`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Indexes for table `charge_rule`
--
ALTER TABLE `charge_rule`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `city`
--
ALTER TABLE `city`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD KEY `district_id` (`district_id`);

--
-- Indexes for table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `company`
--
ALTER TABLE `company`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD UNIQUE KEY `bitmask` (`bitmask`);

--
-- Indexes for table `conductor`
--
ALTER TABLE `conductor`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `person_id` (`person_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`);

--
-- Indexes for table `config`
--
ALTER TABLE `config`
  ADD PRIMARY KEY (`config`);

--
-- Indexes for table `country`
--
ALTER TABLE `country`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Indexes for table `coupon`
--
ALTER TABLE `coupon`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `number` (`number`),
  ADD KEY `client_id` (`client_id`);

--
-- Indexes for table `discount_code`
--
ALTER TABLE `discount_code`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code_2` (`code`),
  ADD KEY `code` (`code`,`scheme_code`,`active`);

--
-- Indexes for table `discount_rule`
--
ALTER TABLE `discount_rule`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `district`
--
ALTER TABLE `district`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD KEY `province_id` (`province_id`);

--
-- Indexes for table `division`
--
ALTER TABLE `division`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD UNIQUE KEY `bitmask` (`bitmask`),
  ADD KEY `company_id` (`company_id`);

--
-- Indexes for table `driver`
--
ALTER TABLE `driver`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `person_id` (`person_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`);

--
-- Indexes for table `markup_rule`
--
ALTER TABLE `markup_rule`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `mobile_device`
--
ALTER TABLE `mobile_device`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `imei` (`imei`),
  ADD KEY `bus_id` (`bus_id`);

--
-- Indexes for table `module`
--
ALTER TABLE `module`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Indexes for table `operational_stage`
--
ALTER TABLE `operational_stage`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD UNIQUE KEY `ordinal` (`ordinal`);

--
-- Indexes for table `passenger`
--
ALTER TABLE `passenger`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `booking_id` (`booking_id`,`idx`),
  ADD KEY `booking_id_2` (`booking_id`);

--
-- Indexes for table `password_reset_token`
--
ALTER TABLE `password_reset_token`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`),
  ADD KEY `write_allowed_divisions` (`write_allowed_divisions`);

--
-- Indexes for table `payment_refund`
--
ALTER TABLE `payment_refund`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `reference` (`reference`),
  ADD KEY `type` (`type`),
  ADD KEY `division_id` (`user_id`);

--
-- Indexes for table `permission`
--
ALTER TABLE `permission`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD KEY `module_id` (`module_id`),
  ADD KEY `type` (`type`);

--
-- Indexes for table `permission_group_permission`
--
ALTER TABLE `permission_group_permission`
  ADD UNIQUE KEY `permission_group_id` (`permission_group_id`,`permission_id`),
  ADD KEY `permission_id` (`permission_id`);

--
-- Indexes for table `person`
--
ALTER TABLE `person`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nic` (`nic`),
  ADD KEY `allowed_divisions` (`allowed_divisions`);

--
-- Indexes for table `province`
--
ALTER TABLE `province`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD KEY `country_code` (`country_id`);

--
-- Indexes for table `refund`
--
ALTER TABLE `refund`
  ADD PRIMARY KEY (`id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`),
  ADD KEY `write_allowed_divisions` (`write_allowed_divisions`);

--
-- Indexes for table `report_param`
--
ALTER TABLE `report_param`
  ADD PRIMARY KEY (`id`),
  ADD KEY `report_id` (`report_id`);

--
-- Indexes for table `report_type`
--
ALTER TABLE `report_type`
  ADD PRIMARY KEY (`id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`);

--
-- Indexes for table `REVINFO`
--
ALTER TABLE `REVINFO`
  ADD PRIMARY KEY (`REV`);

--
-- Indexes for table `rule`
--
ALTER TABLE `rule`
  ADD PRIMARY KEY (`id`),
  ADD KEY `condition` (`rule_condition`);

--
-- Indexes for table `rule_condition`
--
ALTER TABLE `rule_condition`
  ADD PRIMARY KEY (`id`),
  ADD KEY `first_rule` (`first_rule`),
  ADD KEY `second_rule` (`second_rule`);

--
-- Indexes for table `seating_profile`
--
ALTER TABLE `seating_profile`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bus_type_id` (`bus_type_id`);

--
-- Indexes for table `seating_profile_seat`
--
ALTER TABLE `seating_profile_seat`
  ADD PRIMARY KEY (`seating_profile_id`,`seat_number`);

--
-- Indexes for table `service`
--
ALTER TABLE `service`
  ADD PRIMARY KEY (`id`),
  ADD KEY `route_id` (`route_id`),
  ADD KEY `departure_time` (`departure_time`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`),
  ADD UNIQUE KEY `staff_number` (`staff_number`);

--
-- Indexes for table `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`id`),
  ADD KEY `supplier_group_id` (`supplier_group_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`);

--
-- Indexes for table `supplier_account`
--
ALTER TABLE `supplier_account`
  ADD PRIMARY KEY (`id`),
  ADD KEY `supplier_id` (`supplier_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`);

--
-- Indexes for table `supplier_contact_person`
--
ALTER TABLE `supplier_contact_person`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `person_id` (`person_id`),
  ADD KEY `supplier_id` (`supplier_id`),
  ADD KEY `allowed_divisions` (`allowed_divisions`);

--
-- Indexes for table `supplier_group`
--
ALTER TABLE `supplier_group`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `supplier_payment`
--
ALTER TABLE `supplier_payment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `supplier_id` (`supplier_id`);

--
-- Indexes for table `tax_rule`
--
ALTER TABLE `tax_rule`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `travel_class`
--
ALTER TABLE `travel_class`
  ADD PRIMARY KEY (`id`),
  ADD KEY `code` (`code`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `account_status_code` (`account_status_code`),
  ADD KEY `division_id` (`division_id`),
  ADD KEY `agent_id` (`agent_id`);

--
-- Indexes for table `user_group`
--
ALTER TABLE `user_group`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD KEY `division_id` (`division_id`),
  ADD KEY `agent_id` (`agent_id`);

--
-- Indexes for table `user_group_division`
--
ALTER TABLE `user_group_division`
  ADD PRIMARY KEY (`user_group_id`,`division_id`),
  ADD KEY `division_id` (`division_id`);

--
-- Indexes for table `user_group_permission`
--
ALTER TABLE `user_group_permission`
  ADD UNIQUE KEY `user_group_id_2` (`user_group_id`,`permission_id`),
  ADD KEY `permission_id` (`permission_id`);

--
-- Indexes for table `user_login`
--
ALTER TABLE `user_login`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `user_user_group`
--
ALTER TABLE `user_user_group`
  ADD PRIMARY KEY (`user_id`,`user_group_id`),
  ADD KEY `user_group_id` (`user_group_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `account_status`
--
ALTER TABLE `account_status`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `agent`
--
ALTER TABLE `agent`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `agent_allocation`
--
ALTER TABLE `agent_allocation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `agent_restriction`
--
ALTER TABLE `agent_restriction`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `amenity`
--
ALTER TABLE `amenity`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `api_token`
--
ALTER TABLE `api_token`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `booking`
--
ALTER TABLE `booking`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `booking_item`
--
ALTER TABLE `booking_item`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `booking_item_cancellation`
--
ALTER TABLE `booking_item_cancellation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `booking_item_charge`
--
ALTER TABLE `booking_item_charge`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `booking_item_discount`
--
ALTER TABLE `booking_item_discount`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `booking_item_markup`
--
ALTER TABLE `booking_item_markup`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `booking_item_passenger`
--
ALTER TABLE `booking_item_passenger`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `booking_item_tax`
--
ALTER TABLE `booking_item_tax`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `booking_status`
--
ALTER TABLE `booking_status`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus`
--
ALTER TABLE `bus`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_bus_route`
--
ALTER TABLE `bus_bus_route`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_fare`
--
ALTER TABLE `bus_fare`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_held_item`
--
ALTER TABLE `bus_held_item`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_image`
--
ALTER TABLE `bus_image`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_route`
--
ALTER TABLE `bus_route`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_route_bus_stop`
--
ALTER TABLE `bus_route_bus_stop`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_schedule`
--
ALTER TABLE `bus_schedule`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_schedule_bus_stop`
--
ALTER TABLE `bus_schedule_bus_stop`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_seat`
--
ALTER TABLE `bus_seat`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_sector`
--
ALTER TABLE `bus_sector`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_stop`
--
ALTER TABLE `bus_stop`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_type`
--
ALTER TABLE `bus_type`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `cancellation_rule`
--
ALTER TABLE `cancellation_rule`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `change`
--
ALTER TABLE `change`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `change_type`
--
ALTER TABLE `change_type`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `charge_rule`
--
ALTER TABLE `charge_rule`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `city`
--
ALTER TABLE `city`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `client`
--
ALTER TABLE `client`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `company`
--
ALTER TABLE `company`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `conductor`
--
ALTER TABLE `conductor`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `country`
--
ALTER TABLE `country`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `coupon`
--
ALTER TABLE `coupon`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `discount_code`
--
ALTER TABLE `discount_code`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `discount_rule`
--
ALTER TABLE `discount_rule`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `district`
--
ALTER TABLE `district`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `division`
--
ALTER TABLE `division`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `driver`
--
ALTER TABLE `driver`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `markup_rule`
--
ALTER TABLE `markup_rule`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `mobile_device`
--
ALTER TABLE `mobile_device`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `module`
--
ALTER TABLE `module`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `operational_stage`
--
ALTER TABLE `operational_stage`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `passenger`
--
ALTER TABLE `passenger`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `password_reset_token`
--
ALTER TABLE `password_reset_token`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `payment_refund`
--
ALTER TABLE `payment_refund`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `permission`
--
ALTER TABLE `permission`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `person`
--
ALTER TABLE `person`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `province`
--
ALTER TABLE `province`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `report_param`
--
ALTER TABLE `report_param`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `report_type`
--
ALTER TABLE `report_type`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `REVINFO`
--
ALTER TABLE `REVINFO`
  MODIFY `REV` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `rule`
--
ALTER TABLE `rule`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `rule_condition`
--
ALTER TABLE `rule_condition`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `seating_profile`
--
ALTER TABLE `seating_profile`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `service`
--
ALTER TABLE `service`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `staff`
--
ALTER TABLE `staff`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `supplier`
--
ALTER TABLE `supplier`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `supplier_account`
--
ALTER TABLE `supplier_account`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `supplier_contact_person`
--
ALTER TABLE `supplier_contact_person`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `supplier_group`
--
ALTER TABLE `supplier_group`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `supplier_payment`
--
ALTER TABLE `supplier_payment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `tax_rule`
--
ALTER TABLE `tax_rule`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `travel_class`
--
ALTER TABLE `travel_class`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `user_group`
--
ALTER TABLE `user_group`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `user_login`
--
ALTER TABLE `user_login`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `agent_allocation`
--
ALTER TABLE `agent_allocation`
  ADD CONSTRAINT `agent_allocation_ibfk_1` FOREIGN KEY (`bus_route_id`) REFERENCES `bus_route` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `agent_allocation_ibfk_2` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `agent_allocation_seat`
--
ALTER TABLE `agent_allocation_seat`
  ADD CONSTRAINT `agent_allocation_seat_ibfk_1` FOREIGN KEY (`agent_allocation_id`) REFERENCES `agent_allocation` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `agent_restriction`
--
ALTER TABLE `agent_restriction`
  ADD CONSTRAINT `agent_restriction_ibfk_1` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `agent_restriction_ibfk_2` FOREIGN KEY (`bus_id`) REFERENCES `bus` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `api_token`
--
ALTER TABLE `api_token`
  ADD CONSTRAINT `api_token_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON UPDATE CASCADE;

--
-- Constraints for table `aud__booking`
--
ALTER TABLE `aud__booking`
  ADD CONSTRAINT `FK142961AADF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__booking_item`
--
ALTER TABLE `aud__booking_item`
  ADD CONSTRAINT `FKA72F648ADF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__booking_item_cancellation`
--
ALTER TABLE `aud__booking_item_cancellation`
  ADD CONSTRAINT `FK36CF1D9ADF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__booking_item_charge`
--
ALTER TABLE `aud__booking_item_charge`
  ADD CONSTRAINT `FKA1D6F8F6DF74E054` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__booking_item_discount`
--
ALTER TABLE `aud__booking_item_discount`
  ADD CONSTRAINT `FK36B1CDD8DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__booking_item_markup`
--
ALTER TABLE `aud__booking_item_markup`
  ADD CONSTRAINT `FK2F5D3CBFDF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__booking_item_passenger`
--
ALTER TABLE `aud__booking_item_passenger`
  ADD CONSTRAINT `FKA8165AE5DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__booking_item_tax`
--
ALTER TABLE `aud__booking_item_tax`
  ADD CONSTRAINT `FKA1D6F8F6DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__bus`
--
ALTER TABLE `aud__bus`
  ADD CONSTRAINT `FK_fpo3en59wah2q0j6tvytt4byw` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__bus_amenity`
--
ALTER TABLE `aud__bus_amenity`
  ADD CONSTRAINT `FK_lcjkcf9jnoau1uclce8mb7ex4` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__bus_bus_route`
--
ALTER TABLE `aud__bus_bus_route`
  ADD CONSTRAINT `FK_pyvx6na254wodm0ykr0q3cr55` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__change`
--
ALTER TABLE `aud__change`
  ADD CONSTRAINT `FK80F1E981DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__conductor`
--
ALTER TABLE `aud__conductor`
  ADD CONSTRAINT `FK_kwyflw9mwoatb0im2sjwtymit` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__driver`
--
ALTER TABLE `aud__driver`
  ADD CONSTRAINT `FK_bmjha3uk1cofprvsh3gffu6y` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__passenger`
--
ALTER TABLE `aud__passenger`
  ADD CONSTRAINT `FK8BA56BDF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__payment`
--
ALTER TABLE `aud__payment`
  ADD CONSTRAINT `FKA433ECD71D79195C` FOREIGN KEY (`id`,`REV`) REFERENCES `aud__payment_refund` (`id`, `REV`);

--
-- Constraints for table `aud__payment_refund`
--
ALTER TABLE `aud__payment_refund`
  ADD CONSTRAINT `FK60C05522DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__person`
--
ALTER TABLE `aud__person`
  ADD CONSTRAINT `FK_hvhc8n3pslqac4afkiq58y1k7` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

--
-- Constraints for table `aud__refund`
--
ALTER TABLE `aud__refund`
  ADD CONSTRAINT `FK9A0791491D79195C` FOREIGN KEY (`id`,`REV`) REFERENCES `aud__payment_refund` (`id`, `REV`);

--
-- Constraints for table `booking`
--
ALTER TABLE `booking`
  ADD CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`status_code`) REFERENCES `booking_status` (`code`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_ibfk_3` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_ibfk_4` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_ibfk_5` FOREIGN KEY (`division_id`) REFERENCES `division` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `booking_item`
--
ALTER TABLE `booking_item`
  ADD CONSTRAINT `booking_item_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_item_ibfk_2` FOREIGN KEY (`status_code`) REFERENCES `booking_status` (`code`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_item_ibfk_3` FOREIGN KEY (`schedule_id`,`from_bus_stop_id`) REFERENCES `bus_schedule_bus_stop` (`schedule_id`, `bus_stop_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_item_ibfk_4` FOREIGN KEY (`schedule_id`,`to_bus_stop_id`) REFERENCES `bus_schedule_bus_stop` (`schedule_id`, `bus_stop_id`) ON UPDATE CASCADE;

--
-- Constraints for table `booking_item_cancellation`
--
ALTER TABLE `booking_item_cancellation`
  ADD CONSTRAINT `booking_item_cancellation_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_item_cancellation_ibfk_2` FOREIGN KEY (`booking_item_id`) REFERENCES `booking_item` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_item_cancellation_ibfk_3` FOREIGN KEY (`cancellation_scheme_id`) REFERENCES `cancellation_rule` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `booking_item_charge`
--
ALTER TABLE `booking_item_charge`
  ADD CONSTRAINT `booking_item_charge_ibfk_1` FOREIGN KEY (`booking_item_id`) REFERENCES `booking_item` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_item_charge_ibfk_2` FOREIGN KEY (`charge_scheme_id`) REFERENCES `charge_rule` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `booking_item_discount`
--
ALTER TABLE `booking_item_discount`
  ADD CONSTRAINT `booking_item_discount_ibfk_1` FOREIGN KEY (`booking_item_id`) REFERENCES `booking_item` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_item_discount_ibfk_2` FOREIGN KEY (`discount_scheme_id`) REFERENCES `discount_rule` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `booking_item_markup`
--
ALTER TABLE `booking_item_markup`
  ADD CONSTRAINT `booking_item_markup_ibfk_1` FOREIGN KEY (`booking_item_id`) REFERENCES `booking_item` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_item_markup_ibfk_2` FOREIGN KEY (`markup_scheme_id`) REFERENCES `markup_rule` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `booking_item_passenger`
--
ALTER TABLE `booking_item_passenger`
  ADD CONSTRAINT `booking_item_passenger_ibfk_1` FOREIGN KEY (`booking_item_id`) REFERENCES `booking_item` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_item_passenger_ibfk_2` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_item_passenger_ibfk_3` FOREIGN KEY (`status_code`) REFERENCES `booking_status` (`code`) ON UPDATE CASCADE;

--
-- Constraints for table `booking_item_tax`
--
ALTER TABLE `booking_item_tax`
  ADD CONSTRAINT `booking_item_tax_ibfk_1` FOREIGN KEY (`booking_item_id`) REFERENCES `booking_item` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_item_tax_ibfk_2` FOREIGN KEY (`tax_scheme_id`) REFERENCES `tax_rule` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus`
--
ALTER TABLE `bus`
  ADD CONSTRAINT `bus_ibfk_1` FOREIGN KEY (`bus_type_id`) REFERENCES `bus_type` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_ibfk_3` FOREIGN KEY (`driver_id`) REFERENCES `driver` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_ibfk_4` FOREIGN KEY (`conductor_id`) REFERENCES `conductor` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_ibfk_5` FOREIGN KEY (`travel_class_id`) REFERENCES `travel_class` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_ibfk_6` FOREIGN KEY (`division_id`) REFERENCES `division` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_ibfk_7` FOREIGN KEY (`seating_profile_id`) REFERENCES `seating_profile` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_amenity`
--
ALTER TABLE `bus_amenity`
  ADD CONSTRAINT `bus_amenity_ibfk_1` FOREIGN KEY (`bus_id`) REFERENCES `bus` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_amenity_ibfk_2` FOREIGN KEY (`amenity_id`) REFERENCES `amenity` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_bus_route`
--
ALTER TABLE `bus_bus_route`
  ADD CONSTRAINT `bus_bus_route_ibfk_1` FOREIGN KEY (`bus_id`) REFERENCES `bus` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_bus_route_ibfk_2` FOREIGN KEY (`bus_route_id`) REFERENCES `bus_route` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_fare`
--
ALTER TABLE `bus_fare`
  ADD CONSTRAINT `bus_fare_ibfk_1` FOREIGN KEY (`bus_sector_id`) REFERENCES `bus_sector` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_fare_ibfk_2` FOREIGN KEY (`supplier_group_id`) REFERENCES `supplier_group` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_fare_ibfk_3` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_fare_ibfk_4` FOREIGN KEY (`bus_id`) REFERENCES `bus` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_fare_ibfk_5` FOREIGN KEY (`travel_class_id`) REFERENCES `travel_class` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_held_item`
--
ALTER TABLE `bus_held_item`
  ADD CONSTRAINT `bus_held_item_ibfk_1` FOREIGN KEY (`schedule_id`,`from_bus_stop_id`) REFERENCES `bus_schedule_bus_stop` (`schedule_id`, `bus_stop_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_held_item_ibfk_2` FOREIGN KEY (`schedule_id`,`to_bus_stop_id`) REFERENCES `bus_schedule_bus_stop` (`schedule_id`, `bus_stop_id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_held_item_seat`
--
ALTER TABLE `bus_held_item_seat`
  ADD CONSTRAINT `bus_held_item_seat_ibfk_1` FOREIGN KEY (`bus_held_item_id`) REFERENCES `bus_held_item` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `bus_image`
--
ALTER TABLE `bus_image`
  ADD CONSTRAINT `bus_image_ibfk_1` FOREIGN KEY (`bus_id`) REFERENCES `bus` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_mobile_location`
--
ALTER TABLE `bus_mobile_location`
  ADD CONSTRAINT `bus_mobile_location_ibfk_1` FOREIGN KEY (`bus_id`) REFERENCES `bus` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_route`
--
ALTER TABLE `bus_route`
  ADD CONSTRAINT `bus_route_ibfk_1` FOREIGN KEY (`from_city_id`) REFERENCES `city` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_route_ibfk_2` FOREIGN KEY (`to_city_id`) REFERENCES `city` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_route_bus_stop`
--
ALTER TABLE `bus_route_bus_stop`
  ADD CONSTRAINT `bus_route_bus_stop_ibfk_1` FOREIGN KEY (`route_id`) REFERENCES `bus_route` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_route_bus_stop_ibfk_2` FOREIGN KEY (`bus_stop_id`) REFERENCES `bus_stop` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_schedule`
--
ALTER TABLE `bus_schedule`
  ADD CONSTRAINT `bus_schedule_ibfk_1` FOREIGN KEY (`bus_id`) REFERENCES `bus` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_schedule_ibfk_2` FOREIGN KEY (`bus_route_id`) REFERENCES `bus_route` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_schedule_ibfk_3` FOREIGN KEY (`driver_id`) REFERENCES `driver` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_schedule_ibfk_4` FOREIGN KEY (`conductor_id`) REFERENCES `conductor` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_schedule_ibfk_5` FOREIGN KEY (`seating_profile_id`) REFERENCES `seating_profile` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_schedule_ibfk_6` FOREIGN KEY (`stage_id`) REFERENCES `operational_stage` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_schedule_bus_stop`
--
ALTER TABLE `bus_schedule_bus_stop`
  ADD CONSTRAINT `bus_schedule_bus_stop_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `bus_schedule` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_schedule_bus_stop_ibfk_2` FOREIGN KEY (`bus_stop_id`) REFERENCES `bus_stop` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_seat`
--
ALTER TABLE `bus_seat`
  ADD CONSTRAINT `bus_seat_ibfk_1` FOREIGN KEY (`bus_type_id`) REFERENCES `bus_type` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_sector`
--
ALTER TABLE `bus_sector`
  ADD CONSTRAINT `bus_sector_ibfk_1` FOREIGN KEY (`bus_route_id`) REFERENCES `bus_route` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_sector_ibfk_2` FOREIGN KEY (`from_city_id`) REFERENCES `city` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `bus_sector_ibfk_3` FOREIGN KEY (`to_city_id`) REFERENCES `city` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `bus_stop`
--
ALTER TABLE `bus_stop`
  ADD CONSTRAINT `bus_stop_ibfk_1` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `cancellation_rule`
--
ALTER TABLE `cancellation_rule`
  ADD CONSTRAINT `cancellation_rule_ibfk_1` FOREIGN KEY (`id`) REFERENCES `rule` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `change`
--
ALTER TABLE `change`
  ADD CONSTRAINT `change_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `change_ibfk_2` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `change_ibfk_3` FOREIGN KEY (`change_type_id`) REFERENCES `change_type` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `change_ibfk_4` FOREIGN KEY (`booking_item_id`) REFERENCES `booking_item` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `change_ibfk_5` FOREIGN KEY (`booking_item_passenger_id`) REFERENCES `booking_item_passenger` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `charge_rule`
--
ALTER TABLE `charge_rule`
  ADD CONSTRAINT `charge_rule_ibfk_1` FOREIGN KEY (`id`) REFERENCES `rule` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `city`
--
ALTER TABLE `city`
  ADD CONSTRAINT `city_ibfk_1` FOREIGN KEY (`district_id`) REFERENCES `district` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `client`
--
ALTER TABLE `client`
  ADD CONSTRAINT `client_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `conductor`
--
ALTER TABLE `conductor`
  ADD CONSTRAINT `conductor_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `coupon`
--
ALTER TABLE `coupon`
  ADD CONSTRAINT `coupon_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `discount_rule`
--
ALTER TABLE `discount_rule`
  ADD CONSTRAINT `discount_rule_ibfk_1` FOREIGN KEY (`id`) REFERENCES `rule` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `district`
--
ALTER TABLE `district`
  ADD CONSTRAINT `district_ibfk_1` FOREIGN KEY (`province_id`) REFERENCES `province` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `division`
--
ALTER TABLE `division`
  ADD CONSTRAINT `division_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `driver`
--
ALTER TABLE `driver`
  ADD CONSTRAINT `driver_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `markup_rule`
--
ALTER TABLE `markup_rule`
  ADD CONSTRAINT `markup_rule_ibfk_1` FOREIGN KEY (`id`) REFERENCES `rule` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `mobile_device`
--
ALTER TABLE `mobile_device`
  ADD CONSTRAINT `mobile_device_ibfk_1` FOREIGN KEY (`bus_id`) REFERENCES `bus` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `passenger`
--
ALTER TABLE `passenger`
  ADD CONSTRAINT `passenger_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `password_reset_token`
--
ALTER TABLE `password_reset_token`
  ADD CONSTRAINT `password_reset_token_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON UPDATE CASCADE;

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`id`) REFERENCES `payment_refund` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `payment_ibfk_2` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `payment_refund`
--
ALTER TABLE `payment_refund`
  ADD CONSTRAINT `payment_refund_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `permission`
--
ALTER TABLE `permission`
  ADD CONSTRAINT `permission_ibfk_1` FOREIGN KEY (`module_id`) REFERENCES `module` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `permission_group_permission`
--
ALTER TABLE `permission_group_permission`
  ADD CONSTRAINT `permission_group_permission_ibfk_1` FOREIGN KEY (`permission_group_id`) REFERENCES `permission` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `permission_group_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `province`
--
ALTER TABLE `province`
  ADD CONSTRAINT `province_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `refund`
--
ALTER TABLE `refund`
  ADD CONSTRAINT `refund_ibfk_1` FOREIGN KEY (`id`) REFERENCES `payment_refund` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `refund_ibfk_2` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `report_param`
--
ALTER TABLE `report_param`
  ADD CONSTRAINT `report_param_ibfk_1` FOREIGN KEY (`report_id`) REFERENCES `report_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `rule`
--
ALTER TABLE `rule`
  ADD CONSTRAINT `rule_ibfk_1` FOREIGN KEY (`rule_condition`) REFERENCES `rule_condition` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `rule_condition`
--
ALTER TABLE `rule_condition`
  ADD CONSTRAINT `rule_condition_ibfk_1` FOREIGN KEY (`first_rule`) REFERENCES `rule_condition` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `rule_condition_ibfk_2` FOREIGN KEY (`second_rule`) REFERENCES `rule_condition` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `seating_profile`
--
ALTER TABLE `seating_profile`
  ADD CONSTRAINT `seating_profile_ibfk_1` FOREIGN KEY (`bus_type_id`) REFERENCES `bus_type` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `seating_profile_seat`
--
ALTER TABLE `seating_profile_seat`
  ADD CONSTRAINT `seating_profile_seat_ibfk_1` FOREIGN KEY (`seating_profile_id`) REFERENCES `seating_profile` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `service`
--
ALTER TABLE `service`
  ADD CONSTRAINT `service_ibfk_1` FOREIGN KEY (`route_id`) REFERENCES `bus_route` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `staff`
--
ALTER TABLE `staff`
  ADD CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `supplier`
--
ALTER TABLE `supplier`
  ADD CONSTRAINT `supplier_ibfk_1` FOREIGN KEY (`supplier_group_id`) REFERENCES `supplier_group` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `supplier_account`
--
ALTER TABLE `supplier_account`
  ADD CONSTRAINT `supplier_account_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `supplier_contact_person`
--
ALTER TABLE `supplier_contact_person`
  ADD CONSTRAINT `supplier_contact_person_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `supplier_contact_person_ibfk_2` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `supplier_payment`
--
ALTER TABLE `supplier_payment`
  ADD CONSTRAINT `supplier_payment_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `tax_rule`
--
ALTER TABLE `tax_rule`
  ADD CONSTRAINT `tax_rule_ibfk_1` FOREIGN KEY (`id`) REFERENCES `rule` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `user_ibfk_1` FOREIGN KEY (`account_status_code`) REFERENCES `account_status` (`code`) ON UPDATE CASCADE,
  ADD CONSTRAINT `user_ibfk_3` FOREIGN KEY (`division_id`) REFERENCES `division` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `user_ibfk_4` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `user_group`
--
ALTER TABLE `user_group`
  ADD CONSTRAINT `user_group_ibfk_1` FOREIGN KEY (`division_id`) REFERENCES `division` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `user_group_ibfk_2` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `user_group_division`
--
ALTER TABLE `user_group_division`
  ADD CONSTRAINT `user_group_division_ibfk_2` FOREIGN KEY (`division_id`) REFERENCES `division` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `user_group_division_ibfk_3` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `user_group_permission`
--
ALTER TABLE `user_group_permission`
  ADD CONSTRAINT `user_group_permission_ibfk_1` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `user_group_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `user_user_group`
--
ALTER TABLE `user_user_group`
  ADD CONSTRAINT `user_user_group_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `user_user_group_ibfk_2` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`) ON UPDATE CASCADE;

DELIMITER $$
--
-- Events
--
CREATE DEFINER=`bbkservicesadmin`@`%` EVENT `TokenInvalidator` ON SCHEDULE EVERY 30 MINUTE STARTS '2015-01-01 10:30:00' ON COMPLETION NOT PRESERVE ENABLE COMMENT 'Deletes password reset tokens that are more than one day old' DO DELETE FROM `password_reset_token` WHERE `timestamp` < DATE_SUB(NOW(), INTERVAL 1 DAY)$$

DELIMITER ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
