
--
-- Table structure for table `bus_mobile_location`
--

CREATE TABLE `bus_mobile_location` (
 `id` int(10) NOT NULL,
 `bus_id` int(10) NOT NULL,
 `lattitude` decimal(11,8) NOT NULL,
 `longitude` decimal(11,8) NOT NULL,
 `speed` decimal(12,12) NOT NULL DEFAULT '0.000000000000' COMMENT 'Speed in meters/second',
 `bearing` float(5,4) NOT NULL COMMENT 'Bearing of the device. Ranges from 0 - 360. 0.0 returns if no bearing',
 `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bus_schedule_waiting_list`
--

CREATE TABLE `bus_schedule_waiting_list` (
 `id` int(10) NOT NULL,
 `srv_bus_schedule_id` int(10) NOT NULL,
 `name` varchar(250) NOT NULL,
 `email` varchar(250) NOT NULL,
 `phonenumber` varchar(25) NOT NULL,
 `nic` varchar(20) NOT NULL,
 `ip` varchar(20) NOT NULL,
 `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `session`
--

CREATE TABLE `session` (
 `id` char(32) NOT NULL,
 `name` varchar(255) NOT NULL,
 `lifetime` int(11) DEFAULT NULL,
 `modified` int(11) DEFAULT NULL,
 `data` longblob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bus_mobile_location`
--
ALTER TABLE `bus_mobile_location`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `bus_schedule_waiting_list`
--
ALTER TABLE `bus_schedule_waiting_list`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `session`
--
ALTER TABLE `session`
 ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bus_mobile_location`
--
ALTER TABLE `bus_mobile_location`
 MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `bus_schedule_waiting_list`
--
ALTER TABLE `bus_schedule_waiting_list`
 MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;