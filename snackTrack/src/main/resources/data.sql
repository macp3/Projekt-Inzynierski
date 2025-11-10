INSERT INTO admins (id, login, email, password)
VALUES (1, 'admin1', 'admin1@example.com', '$2a$10$CxKxxbf4K0fVX26mtS5U0e2zBCoNqLhQPnwlO3X36RJMiQSosSkuy');

INSERT INTO admins (id, login, email, password)
VALUES (2, 'admin2', 'admin2@example.com', '$2a$10$CxKxxbf4K0fVX26mtS5U0e2zBCoNqLhQPnwlO3X36RJMiQSosSkuy');

INSERT INTO admins (id, login, email, password)
VALUES (3, 'admin3', 'admin3@example.com', '$2a$10$CxKxxbf4K0fVX26mtS5U0e2zBCoNqLhQPnwlO3X36RJMiQSosSkuy');

INSERT INTO exercises (name, description, type, difficulty, number_of_sets, repetitions_per_set) VALUES
('Push-ups', 'Classic push-ups for upper body strength', 'Strength', 1, 3, 15),
('Squats', 'Bodyweight squats for lower body', 'Strength', 1, 3, 20),
('Plank', 'Core strengthening plank', 'Core', 2, 3, 10), -- czas w sekundach
('Jumping Jacks', 'Cardio warm-up exercise', 'Cardio', 1, 3, 20),
('Lunges', 'Forward lunges for legs and glutes', 'Strength', 2, 3, 12);


INSERT INTO diet_types (name) VALUES 
('balanced'),
('keto'),
('low_carb'),
('high_protein'),
('low_fat'),
('vegan'),
('vegetarian'),
('gluten_free'),
('lactose_free');

-- --------------------------------------------------------
-- TABLE: exercises
-- --------------------------------------------------------
INSERT INTO `exercises` (`name`, `description`, `type`, `difficulty`, `number_of_sets`, `repetitions_per_set`) VALUES
('Push-ups', 'Classic push-ups for upper body strength', 'Strength', 2, 3, 12),
('Squats', 'Bodyweight squats to strengthen legs', 'Strength', 2, 3, 15),
('Plank', 'Static hold for core stability', 'Core', 3, 3, 60),
('Jumping Jacks', 'Dynamic cardio warm-up exercise', 'Cardio', 1, 3, 30),
('Burpees', 'Full body explosive movement', 'Strength', 4, 3, 10),
('Lunges', 'Leg exercise for balance and strength', 'Strength', 3, 3, 10),
('Mountain Climbers', 'Cardio + core movement', 'Cardio', 3, 3, 40),
('Sit-ups', 'Abdominal strengthening exercise', 'Core', 2, 3, 20),
('High Knees', 'Cardio drill to improve stamina', 'Cardio', 2, 3, 30),
('Pull-ups', 'Upper back and arm strength exercise', 'Strength', 5, 3, 8),
('Bicycle Crunch', 'Ab rotation movement for obliques', 'Core', 3, 3, 20),
('Jump Squats', 'Explosive squat variation', 'Plyometrics', 4, 3, 10),
('Tricep Dips', 'Arm exercise using bodyweight', 'Strength', 3, 3, 12),
('Flutter Kicks', 'Lower abs endurance movement', 'Core', 2, 3, 25),
('Push-up to Shoulder Tap', 'Balance and coordination strength drill', 'Strength', 4, 3, 10),
('Side Plank', 'Oblique core stabilization', 'Core', 3, 3, 45),
('Wall Sit', 'Isometric leg endurance', 'Strength', 2, 3, 60),
('Jump Rope', 'Cardio stamina improvement', 'Cardio', 2, 3, 60),
('Glute Bridge', 'Strengthens glutes and lower back', 'Strength', 2, 3, 12),
('Step-ups', 'Leg strength and balance', 'Strength', 2, 3, 15),
('Russian Twist', 'Core rotation exercise', 'Core', 3, 3, 20),
('Bear Crawl', 'Full body conditioning', 'Cardio', 4, 3, 20),
('Chest Fly (dumbbells)', 'Upper chest exercise', 'Strength', 3, 3, 12),
('Shoulder Press', 'Shoulder strength', 'Strength', 3, 3, 12),
('Deadlift (light)', 'Posterior chain strengthening', 'Strength', 4, 3, 8),
('Front Kick', 'Leg mobility and strength', 'Strength', 2, 3, 12),
('Side Lunges', 'Leg & glute exercise', 'Strength', 3, 3, 10),
('Wall Push-ups', 'Beginner chest exercise', 'Strength', 1, 3, 12),
('Inchworm', 'Warm-up and core activation', 'Cardio', 2, 3, 8),
('Hamstring Curl', 'Leg strength', 'Strength', 3, 3, 12),
('Jump Lunges', 'Plyometric leg exercise', 'Plyometrics', 4, 3, 12),
('V-Ups', 'Advanced core exercise', 'Core', 4, 3, 15),
('Bear Plank Shoulder Tap', 'Core + upper body', 'Core', 4, 3, 12),
('Skater Jumps', 'Plyometric leg/cardio', 'Plyometrics', 3, 3, 12),
('Standing Calf Raise', 'Lower leg strengthening', 'Strength', 2, 3, 20),
('Arm Circles', 'Shoulder warm-up', 'Warm-up', 1, 3, 20),
('Leg Raises', 'Lower abdominal exercise', 'Core', 3, 3, 15),
('Mountain Climbers Twist', 'Oblique cardio', 'Cardio', 3, 3, 30),
('Hip Thrust', 'Glute strength', 'Strength', 3, 3, 12),
('Dumbbell Row', 'Back strength', 'Strength', 3, 3, 12),
('Superman', 'Lower back strengthening', 'Core', 2, 3, 12),
('Side Leg Raises', 'Hip abduction', 'Strength', 2, 3, 15);

-- --------------------------------------------------------
-- TABLE: trainings_info
-- --------------------------------------------------------
INSERT INTO `trainings_info` (`name`, `description`, `duration_time`) VALUES
('Full Body Beginner', 'Beginner-friendly full body workout for all muscle groups', 30),
('Cardio Blast', 'High-intensity cardio routine to burn calories', 25),
('Core Strength', 'Focused on building abdominal and lower back stability', 20),
('Power Builder', 'Advanced strength routine with compound movements', 40);

-- --------------------------------------------------------
-- TABLE: trainings
-- --------------------------------------------------------
-- Full Body Beginner (training_id = 1)
INSERT INTO `trainings` (`training_id`, `author_id`, `exercise_id`, `day_of_exercise`) VALUES
(1, 1, 1, 1), (1, 1, 2, 1), (1, 1, 18, 1), -- Day 1
(1, 1, 3, 2), (1, 1, 8, 2), (1, 1, 20, 2),  -- Day 2
(1, 1, 13, 3), (1, 1, 19, 3), (1, 1, 21, 3), -- Day 3
(1, 1, 6, 4), (1, 1, 15, 4), (1, 1, 22, 4), -- Day 4
(1, 1, 16, 5), (1, 1, 23, 5), (1, 1, 24, 5), -- Day 5
(1, 1, 25, 6), (1, 1, 26, 6), (1, 1, 27, 6), -- Day 6
(1, 1, 28, 7), (1, 1, 29, 7), (1, 1, 30, 7); -- Day 7

-- Cardio Blast (training_id = 2)
INSERT INTO `trainings` (`training_id`, `author_id`, `exercise_id`, `day_of_exercise`) VALUES
(2, 1, 4, 1), (2, 1, 9, 1), (2, 1, 17, 1),  -- Day 1
(2, 1, 5, 2), (2, 1, 7, 2), (2, 1, 31, 2),   -- Day 2
(2, 1, 8, 3), (2, 1, 20, 3), (2, 1, 32, 3),  -- Day 3
(2, 1, 18, 4), (2, 1, 33, 4), (2, 1, 34, 4), -- Day 4
(2, 1, 21, 5), (2, 1, 22, 5), (2, 1, 35, 5), -- Day 5
(2, 1, 23, 6), (2, 1, 24, 6), (2, 1, 36, 6), -- Day 6
(2, 1, 25, 7), (2, 1, 26, 7), (2, 1, 27, 7); -- Day 7

-- Core Strength (training_id = 3)
INSERT INTO `trainings` (`training_id`, `author_id`, `exercise_id`, `day_of_exercise`) VALUES
(3, 1, 3, 1), (3, 1, 8, 1), (3, 1, 11, 1),
(3, 1, 14, 2), (3, 1, 20, 2), (3, 1, 21, 2),
(3, 1, 32, 3), (3, 1, 33, 3), (3, 1, 34, 3),
(3, 1, 15, 4), (3, 1, 16, 4), (3, 1, 17, 4),
(3, 1, 18, 5), (3, 1, 19, 5), (3, 1, 20, 5),
(3, 1, 21, 6), (3, 1, 22, 6), (3, 1, 23, 6),
(3, 1, 24, 7), (3, 1, 25, 7), (3, 1, 26, 7);

-- Power Builder (training_id = 4)
INSERT INTO `trainings` (`training_id`, `author_id`, `exercise_id`, `day_of_exercise`) VALUES
(4, 1, 1, 1), (4, 1, 10, 1), (4, 1, 23, 1),
(4, 1, 6, 2), (4, 1, 12, 2), (4, 1, 24, 2),
(4, 1, 5, 3), (4, 1, 11, 3), (4, 1, 25, 3),
(4, 1, 7, 4), (4, 1, 14, 4), (4, 1, 26, 4),
(4, 1, 15, 5), (4, 1, 16, 5), (4, 1, 27, 5),
(4, 1, 17, 6), (4, 1, 18, 6), (4, 1, 28, 6),
(4, 1, 19, 7), (4, 1, 20, 7), (4, 1, 29, 7);
