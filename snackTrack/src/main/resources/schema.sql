-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 10, 2025
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- --------------------------------------------------------
-- DATABASE: `snackTrackDB`
-- --------------------------------------------------------

CREATE DATABASE IF NOT EXISTS `snackTrackDB` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `snackTrackDB`;


-- --------------------------------------------------------
-- TABLE: admins
-- --------------------------------------------------------
CREATE TABLE `admins` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(40) NOT NULL UNIQUE,
  `email` varchar(40) NOT NULL UNIQUE,
  `password` varchar(300) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: diet_types
-- --------------------------------------------------------
CREATE TABLE `diet_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` enum('balanced','keto','low_carb','high_protein','low_fat','vegan','vegetarian','gluten_free','lactose_free') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: users
-- --------------------------------------------------------
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `surname` varchar(30) NOT NULL,
  `email` varchar(40) NOT NULL UNIQUE,
  `password` varchar(500) NOT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `premium_expiration` date DEFAULT NULL,
  `status` enum('banned','active','inactive') NOT NULL,
  `preffered_diet` enum('balanced','keto','low_carb','high_protein','low_fat','vegan','vegetarian','gluten_free','lactose_free') DEFAULT NULL,
  `streak` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: body_parameters
-- --------------------------------------------------------
CREATE TABLE `body_parameters` (
  `user_id` int(11) NOT NULL,
  `sex` enum('male','female') NOT NULL,
  `height` float NOT NULL,
  `weight` float NOT NULL,
  `age` int(11) NOT NULL,
  `goal_weight` float NOT NULL,
  `daily_activity_factor` float NOT NULL,
  `daily_activity_training_factor` float NOT NULL,
  `weekly_weight_change_tempo` float NOT NULL,
  `calorie_limit` float NOT NULL,
  `protein_limit` float NOT NULL,
  `fat_limit` float NOT NULL,
  `carbohydrates_limit` float NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `body_parameters_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: badges
-- --------------------------------------------------------
CREATE TABLE `badges` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `badge` varchar(30) DEFAULT NULL,
  `badge_logo` blob DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `badges_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: essential_food
-- --------------------------------------------------------
CREATE TABLE `essential_food` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL UNIQUE,
  `author_id` int(11) NOT NULL,
  `description` varchar(100) NOT NULL,
  `calories` float NOT NULL,
  `protein` float NOT NULL,
  `fat` float NOT NULL,
  `carbohydrates` float NOT NULL,
  `default_weight` float DEFAULT NULL,
  `serving_size_unit` varchar(50) DEFAULT NULL,
  `brand_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `essential_food_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: meals
-- --------------------------------------------------------
CREATE TABLE `meals` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `author_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(100) NOT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `meals_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: meal_diet_types
-- --------------------------------------------------------
CREATE TABLE `meal_diet_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `meal_id` int(11) NOT NULL,
  `diet_type_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `meal_diet_types_meal_fk` FOREIGN KEY (`meal_id`) REFERENCES `meals`(`id`) ON DELETE CASCADE,
  CONSTRAINT `meal_diet_types_diet_fk` FOREIGN KEY (`diet_type_id`) REFERENCES `diet_types`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: ingredients
-- --------------------------------------------------------
CREATE TABLE `ingredients` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `meal_id` int(11) DEFAULT NULL,
  `essential_id` int(11) DEFAULT NULL,
  `essential_api_id` int(11) DEFAULT NULL,
  `amount` float DEFAULT NULL,
  `pieces` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `ingredients_ibfk_1` FOREIGN KEY (`meal_id`) REFERENCES `meals`(`id`) ON DELETE CASCADE,
  CONSTRAINT `ingredients_ibfk_2` FOREIGN KEY (`essential_id`) REFERENCES `essential_food`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: comments
-- --------------------------------------------------------
CREATE TABLE `comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `author_id` int(11) NOT NULL,
  `content` varchar(100) NOT NULL,
  `meal_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`meal_id`) REFERENCES `meals`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: favourite
-- --------------------------------------------------------
CREATE TABLE `favourite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `meal_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `favourite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  CONSTRAINT `favourite_ibfk_2` FOREIGN KEY (`meal_id`) REFERENCES `meals`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: notifications
-- --------------------------------------------------------
CREATE TABLE `notifications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `author_id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `description` varchar(100) NOT NULL,
  `recipients` enum('premium','non_premium','all') NOT NULL,
  `sending_time` date NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `admins`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: registered_alimentation
-- --------------------------------------------------------
CREATE TABLE `registered_alimentation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `essential_id` int(11) DEFAULT NULL,
  `meal_api_id` int(11) DEFAULT NULL,
  `meal_id` int(11) DEFAULT NULL,
  `timestamp` date NOT NULL,
  `meal` enum('breakfast', 'lunch', 'dinner', 'supper', 'snack') NOT NULL,
  `amount` float DEFAULT NULL,
  `pieces` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `registered_alimentation_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  CONSTRAINT `registered_alimentation_ibfk_2` FOREIGN KEY (`essential_id`) REFERENCES `essential_food`(`id`) ON DELETE SET NULL,
  CONSTRAINT `registered_alimentation_ibfk_3` FOREIGN KEY (`meal_id`) REFERENCES `meals`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: reported_comments
-- --------------------------------------------------------
CREATE TABLE `reported_comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reporting_id` int(11) NOT NULL,
  `comment_id` int(11) NOT NULL,
  `content` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `reported_comments_ibfk_1` FOREIGN KEY (`reporting_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  CONSTRAINT `reported_comments_ibfk_2` FOREIGN KEY (`comment_id`) REFERENCES `comments`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: reported_meals
-- --------------------------------------------------------
CREATE TABLE `reported_meals` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reporting_id` int(11) NOT NULL,
  `meal_id` int(11) NOT NULL,
  `content` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `reported_meals_ibfk_1` FOREIGN KEY (`reporting_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  CONSTRAINT `reported_meals_ibfk_2` FOREIGN KEY (`meal_id`) REFERENCES `meals`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: exercises
-- --------------------------------------------------------
CREATE TABLE `exercises` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(100) NOT NULL,
  `type` varchar(50) NOT NULL,
  `difficulty` int(11) NOT NULL,
  `number_of_sets` int(11) NOT NULL,
  `repetitions_per_set` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: trainings_info
-- --------------------------------------------------------
CREATE TABLE `trainings_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(100) NOT NULL,
  `duration_time` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: trainings
-- --------------------------------------------------------
CREATE TABLE `trainings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `training_id` int(11) NOT NULL,
  `author_id` int(11) NOT NULL,
  `exercise_id` int(11) NOT NULL,
  `day_of_exercise` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `trainings_ibfk_1` FOREIGN KEY (`exercise_id`) REFERENCES `exercises`(`id`) ON DELETE CASCADE,
  CONSTRAINT `trainings_ibfk_2` FOREIGN KEY (`author_id`) REFERENCES `admins`(`id`) ON DELETE CASCADE,
  CONSTRAINT `trainings_ibfk_3` FOREIGN KEY (`training_id`) REFERENCES `trainings_info`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- TABLE: user_trainings
-- --------------------------------------------------------
CREATE TABLE `user_trainings` (
  `user_id` int(11) NOT NULL,
  `training_id` int(11) NOT NULL,
  `timestamp` date NOT NULL,
  PRIMARY KEY (`user_id`, `training_id`, `timestamp`),
  CONSTRAINT `user_trainings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  CONSTRAINT `user_trainings_ibfk_2` FOREIGN KEY (`training_id`) REFERENCES `trainings_info`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_device_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    device_token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE verification_token (
  id INT AUTO_INCREMENT PRIMARY KEY,
  token VARCHAR(255) NOT NULL,
  expiry_date DATETIME NOT NULL,
  user_id INT NOT NULL,
  CONSTRAINT fk_verification_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;
COMMIT;
