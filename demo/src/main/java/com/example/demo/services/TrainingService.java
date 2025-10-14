package com.example.demo.services;

import com.example.demo.dto.ExerciseDetailsResponse;
import com.example.demo.dto.TrainingDetailsResponse;
import com.example.demo.dto.TrainingRequest;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingService
{
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
    public Exercise createExercise(String name, String description, String type, int difficulty, int numberOfSets, int repetitionsPerSet)
    {
        if(name == null || name.trim().isBlank())
            throw new IllegalArgumentException("Name must not be empty");
        if(exerciseRepository.existsByName(name.trim()))
            throw new IllegalArgumentException("This exercise already exists");
        if(description == null || description.isBlank())
            throw new IllegalArgumentException("Description must not be empty");
        if(type == null || type.isBlank())
            throw new IllegalArgumentException("Type must not be empty");
        if(difficulty < 1 || difficulty > 3)
            throw new IllegalArgumentException("Difficulty must be 1/2/3");
        if(numberOfSets <= 0 || numberOfSets > 10)
            throw new IllegalArgumentException("Number of sets must be from 1 to 10");
        if(repetitionsPerSet < 1 || repetitionsPerSet > 20)
            throw new IllegalArgumentException("Repetitions per set must be from 1 to 20");

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
    //dorobic delete
    //dorobic edit
    @Transactional
    public void createTraining(TrainingRequest request, int authorId)
    {
        if(request == null || request.getTreningInfo() == null)
            throw new IllegalArgumentException("Training info must not be null");

        boolean exists = trainingInfoRepository.existsByName(request.getTreningInfo().getName());
        if(exists)
            throw new IllegalArgumentException("Training with this name already exists");

        TrainingInfo info = getTrainingInfo(request);

        trainingInfoRepository.save(info);

        if (request.getTrainingExercises() == null || request.getTrainingExercises().isEmpty()) {
            throw new IllegalArgumentException("Training must contain at least one exercise");
        }

        for (TrainingRequest.TrainingExercise ex : request.getTrainingExercises())
        {
            if (ex == null) {
                throw new IllegalArgumentException("Exercise cannot be null");
            }
            if (ex.getExerciseId() <= 0) {
                throw new IllegalArgumentException("Exercise ID must be greater than zero");
            }
            if (ex.getExerciseDay() <= 0) {
                throw new IllegalArgumentException("Day of exercise must be greater than zero");
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

    //validate trainingInfo
    @NotNull
    private static TrainingInfo getTrainingInfo(TrainingRequest request)
    {
        TrainingInfo info = new TrainingInfo();

        if(request.getTreningInfo().getName() == null || request.getTreningInfo().getName().trim().isBlank())
            throw new IllegalArgumentException("Training name must not be empty");
        info.setName(request.getTreningInfo().getName());

        if(request.getTreningInfo().getDescription() == null || request.getTreningInfo().getDescription().trim().isBlank())
            throw new IllegalArgumentException("Training description must not be empty");
        info.setDescription(request.getTreningInfo().getDescription());

        if(request.getTreningInfo().getDurationTime() <= 0)
            throw new IllegalArgumentException("DurationTime must be greater than zero");
        info.setDurationTime(request.getTreningInfo().getDurationTime());
        return info;
    }

    public ExerciseDetailsResponse getExerciseDetails(int exerciseId)
    {
        Exercise exercise = getExerciseById(exerciseId);
        return new ExerciseDetailsResponse(exercise.getId(), exercise.getName(), exercise.getDescription(), exercise.getType(), exercise.getDifficulty(), exercise.getNumberOfSets(), exercise.getRepetitionsPerSet());
    }

    //admin
    //dorobic delete
    @Transactional
    public TrainingDetailsResponse addExerciseToTraining(int exerciseId, int trainingId, int authorId, int dayOfExercise)
    {
        Optional<Admin> optionalAdmin = adminRepository.findById(authorId);
        if(optionalAdmin.isEmpty())
            throw new IllegalArgumentException("Author not found");

        boolean exerciseExists = trainingRepository.existsByTrainingIdAndExerciseIdAndDayOfExercise(trainingId, exerciseId, dayOfExercise);
        if (exerciseExists)
        {
            throw new IllegalArgumentException("This exercise is already assigned to this training on the given day");
        }

        if(dayOfExercise <= 0)
            throw new IllegalArgumentException("Day of exercise must be greater than zero");

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

    public TrainingDetailsResponse getTrainingDetails(int trainingInfoId) {
        TrainingInfo info = trainingInfoRepository.findById(trainingInfoId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

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
    public void assignTrainingToUser(int userId, int trainingId)
    {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        TrainingInfo info = getTrainingInfoById(trainingId);

        // Usuń istniejące przypisania
        List<UserTraining> existing = userTrainingRepository.findByUserId(userId);
        userTrainingRepository.deleteAll(existing);

        // Dodaj nowe przypisanie
        UserTraining newAssignment = new UserTraining();
        newAssignment.setUserId(userId);
        newAssignment.setTrainingId(trainingId);
        newAssignment.setTimestamp(LocalDate.now());

        userTrainingRepository.save(newAssignment);
    }

    public void depriveTrainingFromUser(int userId)
    {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<UserTraining> existing = userTrainingRepository.findByUserId(userId);
        if(existing.isEmpty())
            throw new IllegalArgumentException("This user already has no training assigned");

        userTrainingRepository.deleteAll(existing);
        System.out.println("Training deprived successfully");
    }

    public TrainingInfo getUserTraining(int userId)
    {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        //??????????? dlaczego list
        List<UserTraining> exisiting = userTrainingRepository.findByUserId(user.getId());
        //bierze pierwszy trening (BO TAM JEST TYLKO JEDEN WIEC NIE WIEM CZEMU LISTA)
        UserTraining userTraining = exisiting.get(0);

        return getTrainingInfoById(userTraining.getTrainingId());
    }

    public TrainingInfo getTrainingInfoById(int trainingInfoId)
    {
        return trainingInfoRepository.findById(trainingInfoId).orElseThrow(() -> new IllegalArgumentException("Training info not found"));
    }

    public Exercise getExerciseById(int exerciseId)
    {
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
        return exercise;
    }

    public Training getTrainingById(int trainingId)
    {
        Training training = trainingRepository.findById(trainingId).orElseThrow(() -> new IllegalArgumentException("Training not found"));
        return training;
    }

    public List<TrainingInfo> getAllTrainings()
    {
        return trainingInfoRepository.findAll();
    }

    public List<Exercise> getAllExercises()
    {
        return exerciseRepository.findAll();
    }
}
