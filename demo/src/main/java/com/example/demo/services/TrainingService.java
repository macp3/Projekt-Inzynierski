package com.example.demo.services;

import com.example.demo.dto.TrainingDetailsResponse;
import com.example.demo.dto.TrainingRequest;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    public TrainingService(JwtService jwtService, UserRepository userRepository, TrainingRepository trainingRepository, TrainingInfoRepository trainingInfoRepository, UserTrainingRepository userTrainingRepository, ExerciseRepository exerciseRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.trainingInfoRepository = trainingInfoRepository;
        this.userTrainingRepository = userTrainingRepository;
        this.exerciseRepository = exerciseRepository;
    }

    //admin
    /*@Transactional
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
        exercise.setTrainings(new ArrayList<>());

        exerciseRepository.save(exercise);
        return exercise;
    }*/

    //admin
    public void createTraining(TrainingRequest request, int authorId) {
        // Zapisz treningInfo
        TrainingInfo info = new TrainingInfo();
        info.setName(request.getTreningInfo().getName());
        info.setDescription(request.getTreningInfo().getDescription());
        info.setDurationTime(request.getTreningInfo().getDurationTime());
        trainingInfoRepository.save(info);

        // Zapisz ćwiczenia
        for (TrainingRequest.TrainingExercise ex : request.getTrainingExercises()) {
            Training training = new Training();
            training.setTrainingId(info.getId());
            training.setAuthorId(authorId);
            training.setExerciseId(ex.getExerciseId());
            training.setDayOfExercise(ex.getExerciseDay());
            trainingRepository.save(training);
        }
    }

    //admin
    /*public void addExerciseToTraining(int exerciseId, int trainingId)
    {
        Exercise exercise = getExerciseById(exerciseId);
        Training training = getTrainingById(trainingId);

        training.getExercises().add(exercise);
        exercise.getTrainings().add(training);
        trainingRepository.save(training);
    }*/

   /* public Exercise getExerciseById(int exerciseId)
    {
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
        return exercise;
    }

    public List<Exercise> getAllExercisesByTrainingId(int trainingId)
    {
        Training training = getTrainingById(trainingId);
        return trainingRepository.findAllByTrainingId(training.getId());
    }
    public List<TrainingInfo> getAllTrainingsInfo()
    {
        return trainingInfoRepository.findAll();
    }

    public List<Training> getAllTrainings()
    {
        return trainingRepository.findAll();
    }

    public TrainingInfo getTrainingInfoById(int trainingInfoId)
    {
        return trainingInfoRepository.findById(trainingInfoId).orElseThrow(() -> new IllegalArgumentException("Training info not found"));
    }

    public Training getTrainingById(int trainingId)
    {
        return trainingRepository.findById(trainingId).orElseThrow(() -> new IllegalArgumentException("Training not found"));
    }

    public Exercise getExerciseInfo(int exerciseId)
    {
        return exerciseRepository.findById(exerciseId).orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
    }

    public List<Exercise> getAllExercises()
    {
        return exerciseRepository.findAll();
    }*/

    public List<TrainingInfo> getAllTrainings()
    {
        return trainingInfoRepository.findAll();
    }

    public Training getTrainingById(int trainingId)
    {
        return trainingRepository.findById(trainingId).orElseThrow(() -> new IllegalArgumentException("Training not found"));
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

    public void assignTrainingToUser(int userId, int trainingId) {
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
}
