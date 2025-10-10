-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Paź 05, 2025 at 09:23 PM
-- Wersja serwera: 10.4.32-MariaDB
-- Wersja PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `database`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `admins`
--

CREATE TABLE `admins` (
  `id` int(11) NOT NULL,
  `login` varchar(40) NOT NULL,
  `email` varchar(40) NOT NULL,
  `password` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `badges`
--

CREATE TABLE `badges` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `badge` varchar(30) DEFAULT NULL,
  `badge_logo` blob DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `body_parameters`
--

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
  `carbohydrates_limit` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `comments`
--

CREATE TABLE `comments` (
  `id` int(11) NOT NULL,
  `author_id` int(11) NOT NULL,
  `content` varchar(100) NOT NULL,
  `meal_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `diet_types`
--

CREATE TABLE `diet_types` (
  `id` int(11) NOT NULL,
  `name` enum('balanced','keto', 'low_carb', 'high_protein', 'low_fat', 'vegan', 'vegetarian', 'gluten_free', 'lactose_free') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `essential_food`
--

CREATE TABLE `essential_food` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL UNIQUE,
  `author_id` int(11) NOT NULL,
  `description` varchar(100) NOT NULL,
  `calories` float NOT NULL,
  `protein` float NOT NULL,
  `fat` float NOT NULL,
  `carbohydrates` float NOT NULL,
  `default_weight` float,
  `serving_size_unit` varchar(50),
  `brand_name` varchar(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exercises`
--

CREATE TABLE `exercises` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(100) NOT NULL,
  `type` varchar(50) NOT NULL,
  `difficulty` int(11) NOT NULL,
  `numberOfSets` int(11) NOT NULL,
  `intensivity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `favourite`
--

CREATE TABLE `favourite` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `meal_id` int(11) DEFAULT NULL,
  `meal_api_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `ingredients`
--

CREATE TABLE `ingredients` (
  `id` int(11) NOT NULL,
  `meal_id` int(11) DEFAULT NULL,
  `essential_id` int(11) DEFAULT NULL,
  `essential_api_id` int(11) DEFAULT NULL,
  `amount` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `meals`
--

CREATE TABLE `meals` (
  `id` int(11) NOT NULL,
  `author_id` int(11) NOT NULL,
  `description` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Struktura tabeli dla tabeli `meal_diet_types`
--

CREATE TABLE `meal_diet_types` (
  `id` int(11) NOT NULL,
  `meal_id` int(11) NOT NULL,
  `diet_type_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `notifications`
--

CREATE TABLE `notifications` (
  `id` int(11) NOT NULL,
  `author_id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `description` varchar(100) NOT NULL,
  `recipients` enum('premium','non_premium','all') NOT NULL,
  `sending_time` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `registered_alimentation`
--

CREATE TABLE `registered_alimentation` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `essential_id` int(11) NOT NULL,
  `meal_api_id` int(11) DEFAULT NULL,
  `meal_id` int(11) DEFAULT NULL,
  `timestamp` date NOT NULL,
  `weight` float NOT NULL,
  `amount` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `reported_comments`
--

CREATE TABLE `reported_comments` (
  `id` int(11) NOT NULL,
  `reporting_id` int(11) NOT NULL,
  `comment_id` int(11) NOT NULL,
  `content` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `reported_meals`
--

CREATE TABLE `reported_meals` (
  `id` int(11) NOT NULL,
  `reporting_id` int(11) NOT NULL,
  `meal_id` int(11) NOT NULL,
  `content` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `trainings`
--

CREATE TABLE `trainings` (
  `id` int(11) NOT NULL,
  `training_id` int(11) NOT NULL,
  `author_id` int(11) NOT NULL,
  `exercise_id` int(11) NOT NULL,
  `day_of_exercise` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `trainings_info`
--

CREATE TABLE `trainings_info` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `description` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `surname` varchar(30) NOT NULL,
  `email` varchar(40) NOT NULL,
  `login` varchar(40) NOT NULL,
  `password` varchar(500) NOT NULL,
  `profile_picture` blob DEFAULT NULL,
  `premium_expiration` date DEFAULT NULL,
  `status` enum('banned','active', 'inactive') NOT NULL,
  `preffered_diet` varchar(40) DEFAULT NULL,
  `streak` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `user_trainings`
--

CREATE TABLE `user_trainings` (
  `user_id` int(11) NOT NULL,
  `training_id` int(11) NOT NULL,
  `timestamp` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `login` (`login`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indeksy dla tabeli `badges`
--
ALTER TABLE `badges`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indeksy dla tabeli `body_parameters`
--
ALTER TABLE `body_parameters`
  ADD PRIMARY KEY (`user_id`);

--
-- Indeksy dla tabeli `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `author_id` (`author_id`),
  ADD KEY `meal_id` (`meal_id`);

--
-- Indeksy dla tabeli `diet_types`
--
ALTER TABLE `diet_types`
  ADD PRIMARY KEY (`id`);

--
-- Indeksy dla tabeli `essential_food`
--
ALTER TABLE `essential_food`
  ADD PRIMARY KEY (`id`),
  ADD KEY `author_id` (`author_id`);

--
-- Indeksy dla tabeli `exercises`
--
ALTER TABLE `exercises`
  ADD PRIMARY KEY (`id`);

--
-- Indeksy dla tabeli `favourite`
--
ALTER TABLE `favourite`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `meal_id` (`meal_id`);

--
-- Indeksy dla tabeli `ingredients`
--
ALTER TABLE `ingredients`
  ADD PRIMARY KEY (`id`),
  ADD KEY `meal_id` (`meal_id`),
  ADD KEY `essential_id` (`essential_id`);

--
-- Indeksy dla tabeli `meals`
--
ALTER TABLE `meals`
  ADD PRIMARY KEY (`id`),
  ADD KEY `author_id` (`author_id`);

--
-- Indeksy dla tabeli `meal_diet_types`
--
ALTER TABLE `meal_diet_types`
  ADD PRIMARY KEY (`id`),
  ADD KEY `meal_id` (`meal_id`),
  ADD KEY `diet_type_id` (`diet_type_id`);

--
-- Indeksy dla tabeli `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `author_id` (`author_id`);

--
-- Indeksy dla tabeli `registered_alimentation`
--
ALTER TABLE `registered_alimentation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `essential_id` (`essential_id`),
  ADD KEY `meal_id` (`meal_id`);

--
-- Indeksy dla tabeli `reported_comments`
--
ALTER TABLE `reported_comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `reporting_id` (`reporting_id`),
  ADD KEY `comment_id` (`comment_id`);

--
-- Indeksy dla tabeli `reported_meals`
--
ALTER TABLE `reported_meals`
  ADD PRIMARY KEY (`id`),
  ADD KEY `reporting_id` (`reporting_id`),
  ADD KEY `meal_id` (`meal_id`);

--
-- Indeksy dla tabeli `trainings`
--
ALTER TABLE `trainings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `training_id` (`training_id`),
  ADD KEY `author_id` (`author_id`),
  ADD KEY `excercise_id` (`exercise_id`);

--
-- Indeksy dla tabeli `trainings_info`
--
ALTER TABLE `trainings_info`
  ADD PRIMARY KEY (`id`);

--
-- Indeksy dla tabeli `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `login` (`login`);

--
-- Indeksy dla tabeli `user_trainings`
--
ALTER TABLE `user_trainings`
  ADD PRIMARY KEY (`user_id`),
  ADD KEY `training_id` (`training_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admins`
--
ALTER TABLE `admins`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `badges`
--
ALTER TABLE `badges`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `comments`
--
ALTER TABLE `comments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `essential_food`
--
ALTER TABLE `essential_food`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `exercises`
--
ALTER TABLE `exercises`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `favourite`
--
ALTER TABLE `favourite`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ingredients`
--
ALTER TABLE `ingredients`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `meals`
--
ALTER TABLE `meals`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `registered_alimentation`
--
ALTER TABLE `registered_alimentation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reported_comments`
--
ALTER TABLE `reported_comments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reported_meals`
--
ALTER TABLE `reported_meals`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `trainings`
--
ALTER TABLE `trainings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `trainings_info`
--
ALTER TABLE `trainings_info`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_trainings`
--
ALTER TABLE `user_trainings`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `badges`
--
ALTER TABLE `badges`
  ADD CONSTRAINT `badges_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `body_parameters`
--
ALTER TABLE `body_parameters`
  ADD CONSTRAINT `body_parameters_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `comments`
--
ALTER TABLE `comments`
  ADD CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`meal_id`) REFERENCES `essential_food` (`id`);

--
-- Constraints for table `essential_food`
--
ALTER TABLE `essential_food`
  ADD CONSTRAINT `essential_food_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `favourite`
--
ALTER TABLE `favourite`
  ADD CONSTRAINT `favourite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `favourite_ibfk_2` FOREIGN KEY (`meal_id`) REFERENCES `essential_food` (`id`);

--
-- Constraints for table `ingredients`
--
ALTER TABLE `ingredients`
  ADD CONSTRAINT `ingredients_ibfk_1` FOREIGN KEY (`meal_id`) REFERENCES `meals` (`id`),
  ADD CONSTRAINT `ingredients_ibfk_2` FOREIGN KEY (`essential_id`) REFERENCES `essential_food` (`id`);

--
-- Constraints for table `meals`
--
ALTER TABLE `meals`
  ADD CONSTRAINT `meals_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `meal_diet_types`
--
ALTER TABLE `meal_diet_types`
  ADD CONSTRAINT `meal_diet_types_meal_fk` FOREIGN KEY (`meal_id`) REFERENCES `meals`(`id`),
  ADD CONSTRAINT `meal_diet_types_diet_fk` FOREIGN KEY (`diet_type_id`) REFERENCES `diet_types`(`id`);

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `admins` (`id`);

--
-- Constraints for table `registered_alimentation`
--
ALTER TABLE `registered_alimentation`
  ADD CONSTRAINT `registered_alimentation_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `registered_alimentation_ibfk_2` FOREIGN KEY (`essential_id`) REFERENCES `essential_food` (`id`),
  ADD CONSTRAINT `registered_alimentation_ibfk_3` FOREIGN KEY (`meal_id`) REFERENCES `meals` (`id`);

--
-- Constraints for table `reported_comments`
--
ALTER TABLE `reported_comments`
  ADD CONSTRAINT `reported_comments_ibfk_1` FOREIGN KEY (`reporting_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `reported_comments_ibfk_2` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`);

--
-- Constraints for table `reported_meals`
--
ALTER TABLE `reported_meals`
  ADD CONSTRAINT `reported_meals_ibfk_1` FOREIGN KEY (`reporting_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `reported_meals_ibfk_2` FOREIGN KEY (`meal_id`) REFERENCES `essential_food` (`id`);

--
-- Constraints for table `trainings`
--
ALTER TABLE `trainings`
  ADD CONSTRAINT `trainings_ibfk_1` FOREIGN KEY (`exercise_id`) REFERENCES `exercises` (`id`),
  ADD CONSTRAINT `trainings_ibfk_2` FOREIGN KEY (`author_id`) REFERENCES `admins` (`id`),
  ADD CONSTRAINT `trainings_ibfk_3` FOREIGN KEY (`training_id`) REFERENCES `trainings_info` (`id`);

--
-- Constraints for table `user_trainings`
--
ALTER TABLE `user_trainings`
  ADD CONSTRAINT `user_trainings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `user_trainings_ibfk_2` FOREIGN KEY (`training_id`) REFERENCES `trainings` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
