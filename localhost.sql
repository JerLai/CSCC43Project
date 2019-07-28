
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

--
-- Database: `air_bnb`
--
CREATE DATABASE IF NOT EXISTS `air_bnb`
USE `air_bnb`;

-- --------------------------------------------------------

--
-- Table structure for table `amenities`
--

CREATE TABLE IF NOT EXISTS `amenities` (
  `listingID` int(10) NOT NULL PRIMARY KEY,
  `dining` char(10) NOT NULL,
  `safetyFeatures` char(10) NOT NULL,
  `facilities` char(10) NOT NULL,
  `guestAccess` char(10) NOT NULL,
  `logistics` char(20) NOT NULL,
  `notIncluded` char(20) NOT NULL,
  `bedAndBath` tinyint(1) NOT NULL,
  `outdoor` tinyint(1) NOT NULL,
  `basic` tinyint(1) NOT NULL,
  FOREIGN KEY `listingID` REFERENCES `listing` (`listingID`) ON DELETE CASCADE ON UPDATE CASCADE
)

-- --------------------------------------------------------

--
-- Table structure for table `calendar`
--

CREATE TABLE IF NOT EXISTS `calendar` (
  `listingID` int(10) NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`listingID`, `startDate`),
  FOREIGN KEY REFERENCES `listing` (`listingID`) ON DELETE CASCADE ON UPDATE CASCADE,
)

-- --------------------------------------------------------

--
-- Table structure for table `cancelled`
--

CREATE TABLE IF NOT EXISTS `cancelled` (
  `hostSIN` int(9) NOT NULL,
  `listingID` int(10) NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date NOT NULL,
  `renterSIN` int(9) NOT NULL,
  PRIMARY KEY (`listingID`, `startDate`),
  FOREIGN KEY REFERENCES `listing` (`listingID`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY REFERENCES `startDate` (`startDate`) ON DELETE CASCADE ON UPDATE CASCADE
)

-- --------------------------------------------------------

--
-- Table structure for table `history`,
--

CREATE TABLE IF NOT EXISTS `history` (
  `historyID` int(10) PRIMARY KEY NOT NULL DEFAULT 0 AUTO_INCREMENT,
  `hostSIN` int(9) NOT NULL,
  `renterSIN` int(9) NOT NULL,
  `listingID` int(10) NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date NOT NULL
)

-- --------------------------------------------------------

--
-- Table structure for table `host`
--

CREATE TABLE IF NOT EXISTS `host` (
  `SIN` int(9) PRIMARY KEY NOT NULL,
  FOREIGN KEY (`SIN`) REFERENCES `users` (`SIN`) ON DELETE CASCADE ON UPDATE CASCADE
)

-- --------------------------------------------------------

--
-- Table structure for table `listing`
--

CREATE TABLE IF NOT EXISTS `listing` (
  `listingID` int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY DEFAULT 0,
  `hostSIN` int(9) NOT NULL,
  `type` char(10) NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL,
  `city` varchar(25) NOT NULL,
  `country` varchar(25) NOT NULL,
  `postalCode` char(10) NOT NULL
)

-- --------------------------------------------------------

--
-- Table structure for table `listingrating`
--

CREATE TABLE IF NOT EXISTS `listingrating` (
  `ratingID` int(10) NOT NULL PRIMARY KEY DEFAULT 0,
  `listingID` int(10) NOT NULL,
  `fromSIN` int(9) NOT NULL,
  `rating` double NOT NULL,
  `message` text
)

-- --------------------------------------------------------

--
-- Table structure for table `renter`
--

CREATE TABLE IF NOT EXISTS `renter` (
  `SIN` int(9) PRIMARY KEY NOT NULL,
  `creditCard` int(16) NOT NULL,
  FOREIGN KEY (`SIN`) REFERENCES `users` (`SIN`) ON DELETE CASCADE ON UPDATE CASCADE
)

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE IF NOT EXISTS `reservations` (
  `listingID` int(10) NOT NULL,
  `renterSIN` int(9) NOT NULL,
  `endDate` date NOT NULL,
  `startDate` date NOT NULL,
  PRIMARY KEY (`listingID`, `startDate`),
  FOREIGN KEY REFERENCES `listing` (`listingID`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY REFERENCES `startDate` (`startDate`) ON DELETE CASCADE ON UPDATE CASCADE
)

-- --------------------------------------------------------

--
-- Table structure for table `usercomments`
--

CREATE TABLE IF NOT EXISTS `usercomments` (
  `commentID` int(10) NOT NULL PRIMARY KEY DEFAULT 0 AUTO_INCREMENT,
  `toSIN` int(9) NOT NULL,
  `fromSIN` int(9) NOT NULL,
  `message` text NOT NULL
)

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `SIN` int(9) NOT NULL PRIMARY KEY,
  `name` varchar(15) NOT NULL,
  `address` varchar(35) NOT NULL,
  `occupation` char(20) NOT NULL,
  `DoB` date NOT NULL
)

COMMIT;