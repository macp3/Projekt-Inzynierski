package study.snacktrack.services;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import study.snacktrack.dto.ExerciseDetailsResponse;
import study.snacktrack.dto.TrainingDetailsResponse;
import study.snacktrack.dto.TrainingRequest;
import study.snacktrack.repositories.ExerciseRepository;
import study.snacktrack.repositories.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import study.snacktrack.entities.Admin;
import study.snacktrack.entities.Exercise;
import study.snacktrack.entities.Training;
import study.snacktrack.entities.TrainingInfo;
import study.snacktrack.entities.User;
import study.snacktrack.entities.UserTraining;
import study.snacktrack.repositories.AdminRepository;
import study.snacktrack.repositories.TrainingInfoRepository;
import study.snacktrack.repositories.TrainingRepository;
import study.snacktrack.repositories.UserTrainingRepository;

import jakarta.transaction.Transactional;

@Service
public class TrainingService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingInfoRepository trainingInfoRepository;
    private final UserTrainingRepository userTrainingRepository;
    private final ExerciseRepository exerciseRepository;
    private final AdminRepository adminRepository;

    public TrainingService(JwtService jwtService, UserRepository userRepository, TrainingRepository trainingRepository, TrainingInfoRepository trainingInfoRepository, UserTrainingRepository userTrainingRepository, ExerciseRepository exerciseRepository, AdminRepository adminRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.trainingInfoRepository = trainingInfoRepository;
        this.userTrainingRepository = userTrainingRepository;
        this.exerciseRepository = exerciseRepository;
        this.adminRepository = adminRepository;
    }

    //admin
    @Transactional
    public Exercise createExercise(String name, String description, String type, int difficulty, int numberOfSets, int repetitionsPerSet) {
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

    //admin
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
            if (ex == null) {
                throw new IllegalArgumentException("Exercise cannot be null");
            }
            if (ex.getExerciseId() <= 0) {
                throw new IllegalArgumentException("Exercise ID must be greater than zero");
            }
            if (ex.getExerciseDay() <= 0 || ex.getExerciseDay() > info.getDurationTime()) {
                throw new IllegalArgumentException("Day of exercise must be greater than zero and must be lesser than duration time");
            }
            Training training = new Training();
            training.setTrainingId(info.getId());
            training.setAuthorId(authorId);
            training.setExerciseId(ex.getExerciseId());
            training.setDayOfExercise(ex.getExerciseDay());
            trainingRepository.save(training);
        }
        System.out.println("Training added successfully");
    }

    @Transactional
    public void editTraining(TrainingRequest request, int authorId, int trainingId) {
        if (request == null || request.getTreningInfo() == null)
            throw new IllegalArgumentException("Training info must not be null");

        TrainingInfo existingInfo = trainingInfoRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Training not found"));

        TrainingRequest.TreningInfo updatedInfo = request.getTreningInfo();

        // Sprawdź, czy nowa nazwa jest unikalna (jeśli została podana)
        if (updatedInfo.getName() != null && !updatedInfo.getName().equals(existingInfo.getName())) {
            boolean nameExists = trainingInfoRepository.existsByNameAndIdNot(updatedInfo.getName(), trainingId);
            if (nameExists) {
                throw new IllegalArgumentException("Training with this name already exists");
            }
            existingInfo.setName(updatedInfo.getName());
        }

        // Aktualizuj tylko te pola, które są zdefiniowane
        if (updatedInfo.getDescription() != null) {
            existingInfo.setDescription(updatedInfo.getDescription());
        }

        if (updatedInfo.getDurationTime() != null && updatedInfo.getDurationTime() > 0) {
            existingInfo.setDurationTime(updatedInfo.getDurationTime());
        }

        trainingInfoRepository.save(existingInfo);

        // Jeśli lista ćwiczeń została podana — nadpisujemy
        if (request.getTrainingExercises() != null) {
            trainingRepository.deleteAllByTrainingId(trainingId);

            if (request.getTrainingExercises().isEmpty()) {
                throw new IllegalArgumentException("Training must contain at least one exercise");
            }

            // Ustal aktualny durationTime
            int durationTime = existingInfo.getDurationTime();

            // Walidacja duplikatów ćwiczeń w tym samym dniu
            Set<String> uniqueExerciseDayPairs = new HashSet<>();
            for (TrainingRequest.TrainingExercise ex : request.getTrainingExercises()) {
                if (ex == null || ex.getExerciseId() <= 0 || ex.getExerciseDay() <= 0) {
                    throw new IllegalArgumentException("Invalid exercise data");
                }

                if (ex.getExerciseDay() > durationTime) {
                    throw new IllegalArgumentException("Day of exercise must be between 1 and " + durationTime);
                }

                String key = ex.getExerciseId() + "_" + ex.getExerciseDay();
                if (!uniqueExerciseDayPairs.add(key)) {
                    throw new IllegalArgumentException("You cannot assign the same exercise more than once on the same day");
                }
            }

            // Zapis ćwiczeń
            for (TrainingRequest.TrainingExercise ex : request.getTrainingExercises()) {
                Training training = new Training();
                training.setTrainingId(trainingId);
                training.setAuthorId(authorId);
                training.setExerciseId(ex.getExerciseId());
                training.setDayOfExercise(ex.getExerciseDay());
                trainingRepository.save(training);
            }
        }

        System.out.println("Training edited successfully");
    }



    @Transactional
    public void deleteTraining(int trainingId)
    {
        TrainingInfo info = trainingInfoRepository.findById(trainingId).orElseThrow(() -> new IllegalArgumentException("Training does not exist"));

        trainingRepository.deleteAllByTrainingId(trainingId);
        userTrainingRepository.deleteAllByTrainingId(trainingId);
        trainingInfoRepository.deleteById(trainingId);
        System.out.println("Training deleted successfully");
    }



    //validate trainingInfo
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

        if (request.getTreningInfo().getDurationTime() <= 0) {
            throw new IllegalArgumentException("DurationTime must be greater than zero");
        }
        info.setDurationTime(request.getTreningInfo().getDurationTime());
        return info;
    }

    public ExerciseDetailsResponse getExerciseDetails(int exerciseId) {
        Exercise exercise = getExerciseById(exerciseId);
        return new ExerciseDetailsResponse(exercise.getId(), exercise.getName(), exercise.getDescription(), exercise.getType(), exercise.getDifficulty(), exercise.getNumberOfSets(), exercise.getRepetitionsPerSet());
    }

    //admin
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

        if (dayOfExercise <= 0) {
            throw new IllegalArgumentException("Day of exercise must be greater than zero");
        }

        Exercise exercise = getExerciseById(exerciseId);
        Training training = getTrainingById(trainingId);

        Training newTraining = new Training();
        newTraining.setTrainingId(training.getId());
        newTraining.setAuthorId(optionalAdmin.get().getId());
        newTraining.setExerciseId(exercise.getId());
        newTraining.setDayOfExercise(dayOfExercise);
        trainingRepository.save(newTraining);

        System.out.println("Exercise added to training successfully");
        return getTrainingDetails(training.getId());
    }

    //admin
    @Transactional
    public TrainingDetailsResponse deleteAllExercisesByIdFromTraining(int trainingId, int exerciseId)
    {
        Training training = getTrainingById(trainingId);
        boolean exerciseExists = exerciseRepository.existsById(exerciseId);
        if(!exerciseExists)
            throw new IllegalArgumentException("Exercise not found");
        List<Training> trainings = trainingRepository.findAllByTrainingIdAndExerciseId(trainingId, exerciseId);
        if(trainings.isEmpty())
            throw new IllegalArgumentException("This exercise is not assigned to this training");

        trainingRepository.deleteAll(trainings);
        return getTrainingDetails(training.getId());
    }

    @Transactional
    public TrainingDetailsResponse deleteExerciseByIdAndDayFromTraining(int trainingId, int exerciseId, int dayOfExercise)
    {
        boolean exerciseExists = exerciseRepository.existsById(exerciseId);
        if(!exerciseExists)
            throw new IllegalArgumentException("Exercise not found");
        Training training = trainingRepository.findByTrainingIdAndExerciseIdAndDayOfExercise(trainingId, exerciseId, dayOfExercise).orElseThrow(() -> new IllegalArgumentException("This exercise is not assigned to this training and this day"));

        trainingRepository.delete(training);
        return getTrainingDetails(trainingId);
    }

    public TrainingDetailsResponse getTrainingDetails(int trainingInfoId) {
        TrainingInfo info = trainingInfoRepository.findById(trainingInfoId)
                .orElseThrow(() -> new RuntimeException("Training not found for ID = " +trainingInfoId));

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


    public void depriveTrainingFromUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<UserTraining> existing = userTrainingRepository.findByUserId(userId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("This user already has no training assigned");
        }

        userTrainingRepository.deleteAll(existing);
        System.out.println("Training deprived successfully");
    }

    public TrainingInfo getUserTraining(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UserTraining> userTrainings = userTrainingRepository.findByUserId(userId);
        if (userTrainings.isEmpty()) {
            throw new IllegalArgumentException("This user has no training assigned");
        }

        UserTraining existing = userTrainings.get(0); // jeśli zakładasz tylko jedno przypisanie
        return getTrainingInfoById(existing.getTrainingId());
    }

    @Transactional
    public void deleteExercise(int exerciseId) {
        // Sprawdź, czy ćwiczenie istnieje
        boolean exists = exerciseRepository.existsById(exerciseId);
        if (!exists) {
            throw new IllegalArgumentException("Exercise not found");
        }

        // Usuń wszystkie treningi, które zawierają to ćwiczenie
        List<Training> trainingsToDelete = trainingRepository.findAllByExerciseId(exerciseId);
        if (!trainingsToDelete.isEmpty()) {
            trainingRepository.deleteAll(trainingsToDelete);
        }

        // Usuń ćwiczenie z bazy
        exerciseRepository.deleteById(exerciseId);

        System.out.println("Exercise and related training entries deleted successfully");
    }
    
    public TrainingInfo getTrainingInfoById(int trainingInfoId) {
        return trainingInfoRepository.findById(trainingInfoId).orElseThrow(() -> new IllegalArgumentException("Training info not found"));
    }

    public Exercise getExerciseById(int exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
        return exercise;
    }

    public Training getTrainingById(int trainingId) {
        Training training = trainingRepository.findById(trainingId).orElseThrow(() -> new IllegalArgumentException("Training not found"));
        return training;
    }

    public List<TrainingInfo> getAllTrainings() {
        return trainingInfoRepository.findAll();
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }
}
