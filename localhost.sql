-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jul 20, 2019 at 08:57 PM
-- Server version: 5.7.24-log
-- PHP Version: 7.2.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `air_bnb`
--
CREATE DATABASE IF NOT EXISTS `air_bnb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `air_bnb`;

-- --------------------------------------------------------

--
-- Table structure for table `amenities`
--

CREATE TABLE `amenities` (
  `listingID` int(10) NOT NULL,
  `dining` char(10) NOT NULL,
  `safetyFeatures` char(10) NOT NULL,
  `facilities` char(10) NOT NULL,
  `guestAccess` char(10) NOT NULL,
  `logistics` char(20) NOT NULL,
  `notIncluded` char(20) NOT NULL,
  `bedAndBath` tinyint(1) NOT NULL,
  `outdoor` tinyint(1) NOT NULL,
  `basic` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `calendar`
--

CREATE TABLE `calendar` (
  `listingID` int(10) NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date NOT NULL,
  `price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `cancelled`
--

CREATE TABLE `cancelled` (
  `hostSIN` int(9) NOT NULL,
  `listingID` int(10) NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date NOT NULL,
  `renterSIN` int(9) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `history`
--

CREATE TABLE `history` (
  `historyID` int(10) NOT NULL,
  `hostSIN` int(9) NOT NULL,
  `renterSIN` int(9) NOT NULL,
  `listingID` int(10) NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `host`
--

CREATE TABLE `host` (
  `SIN` int(9) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `listing`
--

CREATE TABLE `listing` (
  `listingID` int(10) NOT NULL,
  `hostSIN` int(9) NOT NULL,
  `type` char(10) NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL,
  `city` varchar(25) NOT NULL,
  `country` varchar(25) NOT NULL,
  `postalCode` char(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `listingrating`
--

CREATE TABLE `listingrating` (
  `ratingID` int(10) NOT NULL,
  `listingID` int(10) NOT NULL,
  `fromSIN` int(9) NOT NULL,
  `rating` double NOT NULL,
  `message` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `renter`
--

CREATE TABLE `renter` (
  `SIN` int(9) NOT NULL,
  `creditCard` int(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `listingID` int(10) NOT NULL,
  `renterSIN` int(9) NOT NULL,
  `endDate` date NOT NULL,
  `startDate` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `usercomments`
--

CREATE TABLE `usercomments` (
  `commentID` int(10) NOT NULL,
  `toSIN` int(9) NOT NULL,
  `fromSIN` int(9) NOT NULL,
  `message` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `SIN` int(9) NOT NULL,
  `name` varchar(15) NOT NULL,
  `address` varchar(35) NOT NULL,
  `occupation` char(20) NOT NULL,
  `DoB` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `amenities`
--
ALTER TABLE `amenities`
  ADD KEY `listingID` (`listingID`);

--
-- Indexes for table `calendar`
--
ALTER TABLE `calendar`
  ADD KEY `listingID` (`listingID`);

--
-- Indexes for table `cancelled`
--
ALTER TABLE `cancelled`
  ADD KEY `listingID` (`listingID`),
  ADD KEY `startDate` (`startDate`,`endDate`);

--
-- Indexes for table `history`
--
ALTER TABLE `history`
  ADD PRIMARY KEY (`historyID`),
  ADD KEY `listingID` (`listingID`),
  ADD KEY `hostSIN` (`hostSIN`);

--
-- Indexes for table `host`
--
ALTER TABLE `host`
  ADD KEY `SIN` (`SIN`);

--
-- Indexes for table `listing`
--
ALTER TABLE `listing`
  ADD PRIMARY KEY (`listingID`),
  ADD KEY `listingID` (`listingID`);

--
-- Indexes for table `listingrating`
--
ALTER TABLE `listingrating`
  ADD PRIMARY KEY (`ratingID`);

--
-- Indexes for table `renter`
--
ALTER TABLE `renter`
  ADD KEY `SIN` (`SIN`);

--
-- Indexes for table `reservations`
--
ALTER TABLE `reservations`
  ADD KEY `listingID` (`listingID`);

--
-- Indexes for table `usercomments`
--
ALTER TABLE `usercomments`
  ADD PRIMARY KEY (`commentID`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`SIN`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `history`
--
ALTER TABLE `history`
  MODIFY `historyID` int(10) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `listing`
--
ALTER TABLE `listing`
  MODIFY `listingID` int(10) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `amenities`
--
ALTER TABLE `amenities`
  ADD CONSTRAINT `amenities_ibfk_1` FOREIGN KEY (`listingID`) REFERENCES `listing` (`listingID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `calendar`
--
ALTER TABLE `calendar`
  ADD CONSTRAINT `calendar_ibfk_1` FOREIGN KEY (`listingID`) REFERENCES `listing` (`listingID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `host`
--
ALTER TABLE `host`
  ADD CONSTRAINT `host_ibfk_1` FOREIGN KEY (`SIN`) REFERENCES `users` (`SIN`);

--
-- Constraints for table `renter`
--
ALTER TABLE `renter`
  ADD CONSTRAINT `renter_ibfk_1` FOREIGN KEY (`SIN`) REFERENCES `users` (`SIN`);

--
-- Constraints for table `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`listingID`) REFERENCES `listing` (`listingID`) ON DELETE CASCADE ON UPDATE CASCADE;
--
-- Database: `test`
--
CREATE DATABASE IF NOT EXISTS `test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `test`;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
