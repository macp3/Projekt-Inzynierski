package study.snacktrack.services;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import study.snacktrack.dto.TrainingDetailsResponse;
import study.snacktrack.dto.TrainingRequest;
import study.snacktrack.repositories.ExerciseRepository;
import study.snacktrack.repositories.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import study.snacktrack.entities.*;
import study.snacktrack.repositories.*;

import jakarta.transaction.Transactional;

/**
 * Service responsible for managing trainings and exercises.
 * Provides functionality for creating, editing, deleting, and assigning trainings to users.
 */
@Service
public class TrainingService {

    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingInfoRepository trainingInfoRepository;
    private final UserTrainingRepository userTrainingRepository;
    private final ExerciseRepository exerciseRepository;
    private final AdminRepository adminRepository;

    /**
     * Constructs TrainingService with required repositories and services.
     */
    public TrainingService(UserRepository userRepository, TrainingRepository trainingRepository,
                           TrainingInfoRepository trainingInfoRepository, UserTrainingRepository userTrainingRepository,
                           ExerciseRepository exerciseRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.trainingInfoRepository = trainingInfoRepository;
        this.userTrainingRepository = userTrainingRepository;
        this.exerciseRepository = exerciseRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * Creates a new exercise (admin only).
     *
     * @param name exercise name
     * @param description exercise description
     * @param type exercise type
     * @param difficulty difficulty level (1-3)
     * @param numberOfSets number of sets
     * @param repetitionsPerSet repetitions per set
     * @return created Exercise entity
     */
    @Transactional
    public Exercise createExercise(String name, String description, String type, int difficulty,
                                   int numberOfSets, int repetitionsPerSet) {
        if (name == null || name.trim().isBlank()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        if (exerciseRepository.existsByName(name.trim())) {
            throw new IllegalArgumentException("This exercise already exists");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description must not be empty");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type must not be empty");
        }
        if (difficulty < 1 || difficulty > 3) {
            throw new IllegalArgumentException("Difficulty must be 1/2/3");
        }
        if (numberOfSets <= 0 || numberOfSets > 10) {
            throw new IllegalArgumentException("Number of sets must be from 1 to 10");
        }
        if (repetitionsPerSet < 1 || repetitionsPerSet > 20) {
            throw new IllegalArgumentException("Repetitions per set must be from 1 to 20");
        }

        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setDescription(description);
        exercise.setType(type);
        exercise.setDifficulty(difficulty);
        exercise.setNumberOfSets(numberOfSets);
        exercise.setRepetitionsPerSet(repetitionsPerSet);

        exerciseRepository.save(exercise);
        return exercise;
    }

    /**
     * Creates a new training with exercises (admin only).
     *
     * @param request training request data
     * @param authorId author identifier
     */
    @Transactional
    public void createTraining(TrainingRequest request, int authorId) {
        if (request == null || request.getTreningInfo() == null) {
            throw new IllegalArgumentException("Training info must not be null");
        }

        boolean exists = trainingInfoRepository.existsByName(request.getTreningInfo().getName());
        if (exists) {
            throw new IllegalArgumentException("Training with this name already exists");
        }

        TrainingInfo info = getTrainingInfo(request);
        trainingInfoRepository.save(info);

        if (request.getTrainingExercises() == null || request.getTrainingExercises().isEmpty()) {
            throw new IllegalArgumentException("Training must contain at least one exercise");
        }

        for (TrainingRequest.TrainingExercise ex : request.getTrainingExercises()) {
            if (!exerciseRepository.existsById(ex.getExerciseId())) {
                throw new IllegalArgumentException("Exercise with ID " + ex.getExerciseId() + " does not exist");
            }
            if (ex.getExerciseDay() <= 0 || ex.getExerciseDay() > info.getDurationTime()) {
                throw new IllegalArgumentException("Day of exercise must be valid");
            }

            Training training = new Training();
            training.setTrainingId(info.getId());
            training.setAuthorId(authorId);
            training.setExerciseId(ex.getExerciseId());
            training.setDayOfExercise(ex.getExerciseDay());
            trainingRepository.save(training);
        }
    }

    /**
     * Edits an existing training (admin only).
     *
     * @param request updated training request
     * @param authorId author identifier
     * @param trainingId training identifier
     */
    @Transactional
    public void editTraining(TrainingRequest request, int authorId, int trainingId) {
        if (request == null || request.getTreningInfo() == null)
            throw new IllegalArgumentException("Training info must not be null");

        TrainingInfo existingInfo = trainingInfoRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Training not found"));

        TrainingRequest.TreningInfo updatedInfo = request.getTreningInfo();

        if (updatedInfo.getName() != null && !updatedInfo.getName().equals(existingInfo.getName())) {
            boolean nameExists = trainingInfoRepository.existsByNameAndIdNot(updatedInfo.getName(), trainingId);
            if (nameExists) {
                throw new IllegalArgumentException("Training with this name already exists");
            }
            existingInfo.setName(updatedInfo.getName());
        }

        if (updatedInfo.getDescription() != null) {
            existingInfo.setDescription(updatedInfo.getDescription());
        }

        if (updatedInfo.getDurationTime() != null && updatedInfo.getDurationTime() > 0) {
            existingInfo.setDurationTime(updatedInfo.getDurationTime());
        }

        trainingInfoRepository.save(existingInfo);

        if (request.getTrainingExercises() != null) {
            trainingRepository.deleteAllByTrainingId(trainingId);

            if (request.getTrainingExercises().isEmpty()) {
                throw new IllegalArgumentException("Training must contain at least one exercise");
            }

            int durationTime = existingInfo.getDurationTime();
            Set<String> uniqueExerciseDayPairs = new HashSet<>();

            for (TrainingRequest.TrainingExercise ex : request.getTrainingExercises()) {
                if (!exerciseRepository.existsById(ex.getExerciseId())) {
                    throw new IllegalArgumentException("Exercise with ID " + ex.getExerciseId() + " does not exist");
                }
                if (ex.getExerciseDay() > durationTime) {
                    throw new IllegalArgumentException("Day of exercise must be between 1 and " + durationTime);
                }
                String key = ex.getExerciseId() + "_" + ex.getExerciseDay();
                if (!uniqueExerciseDayPairs.add(key)) {
                    throw new IllegalArgumentException("Duplicate exercise assignment on the same day");
                }
            }

            for (TrainingRequest.TrainingExercise ex : request.getTrainingExercises()) {
                Training training = new Training();
                training.setTrainingId(trainingId);
                training.setAuthorId(authorId);
                training.setExerciseId(ex.getExerciseId());
                training.setDayOfExercise(ex.getExerciseDay());
                trainingRepository.save(training);
            }
        }
    }

    /**
     * Deletes a training and its related entries.
     *
     * @param trainingId training identifier
     */
    @Transactional
    public void deleteTraining(int trainingId) {
        TrainingInfo info = trainingInfoRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Training does not exist"));

        trainingRepository.deleteAllByTrainingId(trainingId);
        userTrainingRepository.deleteAllByTrainingId(trainingId);
        trainingInfoRepository.deleteById(trainingId);
    }

    /**
     * Validates and builds TrainingInfo from request.
     *
     * @param request training request
     * @return TrainingInfo entity
     */
    @NotNull
    private static TrainingInfo getTrainingInfo(TrainingRequest request) {
        TrainingInfo info = new TrainingInfo();

        if (request.getTreningInfo().getName() == null || request.getTreningInfo().getName().trim().isBlank()) {
            throw new IllegalArgumentException("Training name must not be empty");
        }
        info.setName(request.getTreningInfo().getName());

        if (request.getTreningInfo().getDescription() == null || request.getTreningInfo().getDescription().trim().isBlank()) {
            throw new IllegalArgumentException("Training description must not be empty");
        }
        info.setDescription(request.getTreningInfo().getDescription());

        if (request.getTreningInfo().getDurationTime() == null || request.getTreningInfo().getDurationTime() <= 0) {
            throw new IllegalArgumentException("DurationTime must be greater than zero");
        }
        info.setDurationTime(request.getTreningInfo().getDurationTime());
        return info;
    }

    /**
     * Adds an exercise to a training on a specific day (admin only).
     *
     * @param exerciseId exercise identifier
     * @param trainingId training identifier
     * @param authorId admin identifier
     * @param dayOfExercise day of exercise
     * @return updated training details
     */
    @Transactional
    public TrainingDetailsResponse addExerciseToTraining(int exerciseId, int trainingId, int authorId, int dayOfExercise) {
        Optional<Admin> optionalAdmin = adminRepository.findById(authorId);
        if (optionalAdmin.isEmpty()) {
            throw new IllegalArgumentException("Author not found");
        }

        boolean exerciseExists = trainingRepository.existsByTrainingIdAndExerciseIdAndDayOfExercise(trainingId, exerciseId, dayOfExercise);
        if (exerciseExists) {
            throw new IllegalArgumentException("This exercise is already assigned to this training on the given day");
        }

        TrainingInfo info = getTrainingInfoById(trainingId);

        if (dayOfExercise <= 0 || dayOfExercise > info.getDurationTime()) {
            throw new IllegalArgumentException("Day of exercise must be greater than zero and lesser than duration time");
        }

        Exercise exercise = getExerciseById(exerciseId);

        Training newTraining = new Training();
        newTraining.setTrainingId(info.getId());
        newTraining.setAuthorId(optionalAdmin.get().getId());
        newTraining.setExerciseId(exercise.getId());
        newTraining.setDayOfExercise(dayOfExercise);
        trainingRepository.save(newTraining);

        return getTrainingDetails(info.getId());
    }

    /**
     * Deletes all occurrences of an exercise from a training (admin only).
     *
     * @param trainingId training identifier
     * @param exerciseId exercise identifier
     * @return updated training details
     */
    @Transactional
    public TrainingDetailsResponse deleteAllExercisesByIdFromTraining(int trainingId, int exerciseId) {
        TrainingInfo info = getTrainingInfoById(trainingId);
        boolean exerciseExists = exerciseRepository.existsById(exerciseId);
        if (!exerciseExists)
            throw new IllegalArgumentException("Exercise not found");

        List<Training> trainings = trainingRepository.findAllByTrainingIdAndExerciseId(trainingId, exerciseId);
        if (trainings.isEmpty())
            throw new IllegalArgumentException("This exercise is not assigned to this training");

        trainingRepository.deleteAll(trainings);
        return getTrainingDetails(info.getId());
    }

    /**
     * Deletes a specific exercise from a training on a given day (admin only).
     *
     * @param trainingId training identifier
     * @param exerciseId exercise identifier
     * @param dayOfExercise day of exercise
     * @return updated training details
     */
    @Transactional
    public TrainingDetailsResponse deleteExerciseByIdAndDayFromTraining(int trainingId, int exerciseId, int dayOfExercise) {
        boolean exerciseExists = exerciseRepository.existsById(exerciseId);
        if (!exerciseExists)
            throw new IllegalArgumentException("Exercise not found");

        Training training = trainingRepository.findByTrainingIdAndExerciseIdAndDayOfExercise(trainingId, exerciseId, dayOfExercise)
                .orElseThrow(() -> new IllegalArgumentException("This exercise is not assigned to this training and this day"));

        trainingRepository.delete(training);
        return getTrainingDetails(trainingId);
    }

    /**
     * Retrieves training details including exercises.
     *
     * @param trainingInfoId training info identifier
     * @return training details response DTO
     */
    public TrainingDetailsResponse getTrainingDetails(int trainingInfoId) {
        TrainingInfo info = trainingInfoRepository.findById(trainingInfoId)
                .orElseThrow(() -> new RuntimeException("Training not found for ID = " + trainingInfoId));

        List<Training> trainingEntries = trainingRepository.findByTrainingId(trainingInfoId);

        List<TrainingDetailsResponse.ExerciseWithDay> exercises = trainingEntries.stream()
                .map(entry -> {
                    Exercise exercise = exerciseRepository.findById(entry.getExerciseId())
                            .orElseThrow(() -> new RuntimeException("Exercise not found"));

                    TrainingDetailsResponse.ExerciseWithDay dto = new TrainingDetailsResponse.ExerciseWithDay();
                    dto.setDayOfExercise(entry.getDayOfExercise());
                    dto.setExercise(exercise);
                    return dto;
                })
                .collect(Collectors.toList());

        TrainingDetailsResponse response = new TrainingDetailsResponse();
        response.setTrainingInfo(info);
        response.setExercises(exercises);
        return response;
    }

    /**
     * Assigns a training to a user.
     *
     * @param userId user identifier
     * @param trainingId training identifier
     */
    @Transactional
    public void assignTrainingToUser(int userId, int trainingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        TrainingInfo info = trainingInfoRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("TrainingInfo not found with ID: " + trainingId));

        List<UserTraining> existing = userTrainingRepository.findByUserId(userId);
        if (existing == null) {
            throw new IllegalStateException("UserTraining list is null for user ID: " + userId);
        }

        userTrainingRepository.deleteAll(existing);

        UserTraining newAssignment = new UserTraining();
        newAssignment.setUserId(userId);
        newAssignment.setTrainingId(trainingId);
        newAssignment.setTimestamp(LocalDate.now());

        userTrainingRepository.save(newAssignment);
    }

    /**
     * Removes training assignment from a user.
     *
     * @param userId user identifier
     */
    public void depriveTrainingFromUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<UserTraining> existing = userTrainingRepository.findByUserId(userId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("This user already has no training assigned");
        }

        userTrainingRepository.deleteAll(existing);
    }

    /**
     * Retrieves training info assigned to a user.
     *
     * @param userId user identifier
     * @return TrainingInfo entity
     */
    public TrainingInfo getUserTraining(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UserTraining> userTrainings = userTrainingRepository.findByUserId(userId);
        if (userTrainings.isEmpty()) {
            throw new IllegalArgumentException("This user has no training assigned");
        }

        UserTraining existing = userTrainings.get(0);
        return getTrainingInfoById(existing.getTrainingId());
    }

    /**
     * Deletes an exercise and all related training entries.
     *
     * @param exerciseId exercise identifier
     */
    @Transactional
    public void deleteExercise(int exerciseId) {
        boolean exists = exerciseRepository.existsById(exerciseId);
        if (!exists) {
            throw new IllegalArgumentException("Exercise not found");
        }

        List<Training> trainingsToDelete = trainingRepository.findAllByExerciseId(exerciseId);
        if (!trainingsToDelete.isEmpty()) {
            trainingRepository.deleteAll(trainingsToDelete);
        }

        exerciseRepository.deleteById(exerciseId);
    }

    /**
     * Retrieves training info by ID.
     *
     * @param trainingInfoId training info identifier
     * @return TrainingInfo entity
     */
    public TrainingInfo getTrainingInfoById(int trainingInfoId) {
        return trainingInfoRepository.findById(trainingInfoId)
                .orElseThrow(() -> new IllegalArgumentException("Training info not found"));
    }

    /**
     * Retrieves exercise by ID.
     *
     * @param exerciseId exercise identifier
     * @return Exercise entity
     */
    public Exercise getExerciseById(int exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
    }

    /**
     * Retrieves all training infos.
     *
     * @return list of TrainingInfo entities
     */
    public List<TrainingInfo> getAllTrainings() {
        return trainingInfoRepository.findAll();
    }

    /**
     * Retrieves all exercises.
     *
     * @return list of Exercise entities
     */
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }
}
