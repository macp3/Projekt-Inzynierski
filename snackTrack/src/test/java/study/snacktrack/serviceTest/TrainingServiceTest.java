package study.snacktrack.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import study.snacktrack.dto.TrainingDetailsResponse;
import study.snacktrack.services.*;
import study.snacktrack.entities.*;
import study.snacktrack.repositories.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the TrainingService, covering the management of training plans, including adding exercises, assigning plans to users, and retrieving details.
 * This class ensures that the service methods correctly interact with multiple repositories and enforce assignment logic.
 */
class TrainingServiceTest {

    private TrainingService service;
    private TrainingRepository trainingRepository;
    private TrainingInfoRepository trainingInfoRepository;
    private UserTrainingRepository userTrainingRepository;
    private ExerciseRepository exerciseRepository;
    private AdminRepository adminRepository;
    private UserRepository userRepository;

    /**
     * Sets up mock repositories and initializes the TrainingService instance before each test.
     * This ensures the service is tested in isolation from the actual database layers.
     */
    @BeforeEach
    void setUp() {
        trainingRepository = mock(TrainingRepository.class);
        trainingInfoRepository = mock(TrainingInfoRepository.class);
        userTrainingRepository = mock(UserTrainingRepository.class);
        exerciseRepository = mock(ExerciseRepository.class);
        adminRepository = mock(AdminRepository.class);
        userRepository = mock(UserRepository.class);

        service = new TrainingService(userRepository, trainingRepository,
                trainingInfoRepository, userTrainingRepository, exerciseRepository, adminRepository);
    }

    /**
     * Tests the successful addition of an exercise to an existing training plan by an authorized admin.
     * It verifies that the service checks for existing entries and persists a new {@code Training} record.
     */
    @Test
    void addExerciseToTraining_shouldSaveWhenValid() {
        Admin admin = new Admin();
        admin.setId(99);
        when(adminRepository.findById(99)).thenReturn(Optional.of(admin));

        TrainingInfo info = new TrainingInfo();
        info.setId(5);
        info.setDurationTime(3);
        when(trainingInfoRepository.findById(5)).thenReturn(Optional.of(info));

        Exercise ex = new Exercise();
        ex.setId(1);
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(ex));

        when(trainingRepository.existsByTrainingIdAndExerciseIdAndDayOfExercise(5, 1, 2)).thenReturn(false);

        TrainingDetailsResponse response = service.addExerciseToTraining(1, 5, 99, 2);

        verify(trainingRepository).save(any(Training.class));
        assertEquals(5, response.getTrainingInfo().getId());
    }

    /**
     * Tests the successful removal of all entries for a specific exercise within a training plan.
     * It verifies that the service fetches all relevant {@code Training} records and deletes them in a batch.
     */
    @Test
    void deleteAllExercisesByIdFromTraining_shouldDelete() {
        TrainingInfo info = new TrainingInfo();
        info.setId(5);
        info.setDurationTime(3);
        when(trainingInfoRepository.findById(5)).thenReturn(Optional.of(info));
        when(exerciseRepository.existsById(1)).thenReturn(true);

        Training training = new Training();
        training.setId(10);
        training.setTrainingId(5);
        training.setExerciseId(1);

        when(trainingRepository.findAllByTrainingIdAndExerciseId(5, 1)).thenReturn(List.of(training));

        service.deleteAllExercisesByIdFromTraining(5, 1);

        verify(trainingRepository).deleteAll(List.of(training));
    }

    /**
     * Tests the successful assignment of a training plan to a user.
     * It verifies that the service creates and saves a new {@code UserTraining} assignment record.
     */
    @Test
    void assignTrainingToUser_shouldSaveAssignment() {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        TrainingInfo info = new TrainingInfo();
        info.setId(5);
        when(trainingInfoRepository.findById(5)).thenReturn(Optional.of(info));

        when(userTrainingRepository.findByUserId(1)).thenReturn(List.of());

        service.assignTrainingToUser(1, 5);

        verify(userTrainingRepository).save(any(UserTraining.class));
    }

    /**
     * Tests the process of depriving a user of their current training assignment.
     * It verifies that the service fetches all current {@code UserTraining} records for the user and performs a batch deletion.
     */
    @Test
    void depriveTrainingFromUser_shouldDeleteAssignments() {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserTraining ut = new UserTraining();
        ut.setUserId(1);
        ut.setTrainingId(5);
        when(userTrainingRepository.findByUserId(1)).thenReturn(List.of(ut));

        service.depriveTrainingFromUser(1);

        verify(userTrainingRepository).deleteAll(List.of(ut));
    }

    /**
     * Tests the retrieval of detailed information about a specific training plan, including its exercises.
     * It verifies that the service correctly fetches {@code TrainingInfo} and all associated {@code Training} records, mapping them to the final DTO.
     */
    @Test
    void getTrainingDetails_shouldReturnExercises() {
        TrainingInfo info = new TrainingInfo();
        info.setId(5);
        info.setDurationTime(3);
        when(trainingInfoRepository.findById(5)).thenReturn(Optional.of(info));

        Training training = new Training();
        training.setTrainingId(5);
        training.setExerciseId(1);
        training.setDayOfExercise(2);
        when(trainingRepository.findByTrainingId(5)).thenReturn(List.of(training));

        Exercise ex = new Exercise();
        ex.setId(1);
        ex.setName("Pushup");
        when(exerciseRepository.findById(1)).thenReturn(Optional.of(ex));

        TrainingDetailsResponse response = service.getTrainingDetails(5);

        assertEquals(5, response.getTrainingInfo().getId());
        assertEquals("Pushup", response.getExercises().get(0).getExercise().getName());
    }
}