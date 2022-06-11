-- phpMyAdmin SQL Dump
-- version 3.4.10.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 13, 2015 at 08:32 PM
-- Server version: 5.5.40
-- PHP Version: 5.5.20-1+deb.sury.org~precise+1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `expresslk`
--

--
-- Dumping data for table `account_status`
--

INSERT INTO `account_status` (`id`, `code`, `name`) VALUES
(1, 'ACT', 'Active');

--
-- Dumping data for table `booking_status`
--

INSERT INTO `booking_status` (`id`, `code`, `name`) VALUES
(1, 'CONF', 'Confirmed'),
(2, 'CANC', 'Cancelled'),
(3, 'TENT', 'Tentative booking');

--
-- Dumping data for table `change_type`
--

INSERT INTO `change_type` (`id`, `code`, `name`) VALUES
(1, 'CANC', 'Booking item cancelled'),
(2, 'REOP', 'Reopening a booking/item');

--
-- Dumping data for table `config`
--

INSERT INTO `config` (`config`, `value`) VALUES
('SYS_PASSWORD', 'rbpFrqvCruwPSwKN');

--
-- Dumping data for table `country`
--

INSERT INTO `country` (`id`, `code`, `name`) VALUES
(1, 'LK', 'Sri Lanka');

--
-- Dumping data for table `province`
--

INSERT INTO `province` (`id`, `code`, `name`, `country_id`) VALUES
(1, 'WP', 'Western', 1),
(2, 'EP', 'Eastern', 1),
(3, 'NP', 'Nothern', 1),
(4, 'SP', 'Southern', 1),
(5, 'UP', 'Uva', 1),
(6, 'SG', 'Sabaragamuwa', 1),
(7, 'NW', 'North West', 1),
(8, 'NC', 'North Central', 1),
(9, 'CP', 'Central', 1);
--
-- Dumping data for table `district`
--

INSERT INTO `district` (`id`, `code`, `name`, `province_id`) VALUES
(1, 'CMB', 'Colombo', 1),
(2, 'GMP', 'Gampaha', 1),
(3, 'GAL', 'Galle', 4),
(4, 'MTR', 'Matara', 4),
(5, 'KLT', 'Kalutara', 1),
(6, 'KND', 'Kandy', 9),
(7, 'HMB', 'Hambantota', 4),
(8, 'MNG', 'Monaragala', 5),
(9, 'RTN', 'Ratnapura', 6);

--
-- Dumping data for table `module`
--

INSERT INTO `module` (`id`, `code`, `name`) VALUES
(1, 'SEARCH', 'Search');

--
-- Dumping data for table `permission`
--

INSERT INTO `permission` (`id`, `code`, `name`, `description`, `module_id`) VALUES
(1, 'HOLD', 'Hold permission', 'Allows the user to hold resources', 1);



--
-- Dumping data for table `travel_class`
--

INSERT INTO `travel_class` (`id`, `code`, `name`, `description`) VALUES
(1, 'XL', 'Super Luxury', 'Fully Air-conditioned Super Luxury Bus Service'),
(2, 'L', 'Luxury', 'Air-conditioned Luxury Bus Service '),
(3, 'S', 'Semi Luxury', 'Semi Luxury Bus Service '),
(4, 'N', 'Normal Service', 'Normal Bus Service with Basic Facilities');

--
-- Dumping data for table `user_group`
--

INSERT INTO `user_group` (`id`, `code`, `name`, `description`) VALUES
(1, 'ADM', 'Admin', 'System administrators'),
(2, 'STF', 'PoS staff', 'Point of sales staff member'),
(3, 'AGT', 'Agent', 'Authorized agent'),
(4, 'CLT', 'Client', 'Registered client'),
(5, 'MGR', 'Manager', 'Terminal manager');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
