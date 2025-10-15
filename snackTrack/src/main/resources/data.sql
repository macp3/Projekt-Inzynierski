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