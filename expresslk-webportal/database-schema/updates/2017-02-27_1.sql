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

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bus_schedule_waiting_list`
--
ALTER TABLE `bus_schedule_waiting_list`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bus_schedule_waiting_list`
--
ALTER TABLE `bus_schedule_waiting_list`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;