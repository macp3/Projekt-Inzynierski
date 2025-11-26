package study.snacktrack.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import study.snacktrack.controllers.TrainingController;
import study.snacktrack.dto.TrainingDetailsResponse;
import study.snacktrack.entities.Exercise;
import study.snacktrack.entities.TrainingInfo;
import study.snacktrack.entities.User;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.TrainingService;
import study.snacktrack.services.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingControllerTest {

    private UserService userService;
    private JwtService jwtService;
    private TrainingService trainingService;
    private TrainingController controller;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        jwtService = mock(JwtService.class);
        trainingService = mock(TrainingService.class);

        controller = new TrainingController(userService, jwtService, trainingService);

        // Stub autoryzacji
        when(jwtService.extractEmail("token")).thenReturn("user@example.com");
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
    }

    @Test
    void getAllTrainings_shouldReturnOk() {
        TrainingInfo info = new TrainingInfo();
        info.setId(1);
        info.setName("Cardio");
        info.setDescription("Basic cardio");
        info.setDurationTime(30);

        when(trainingService.getAllTrainings()).thenReturn(List.of(info));

        ResponseEntity<?> response = controller.getAllTrainings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void getExerciseDetails_shouldReturnOk() {
        Exercise ex = new Exercise();
        ex.setId(5);
        ex.setName("Push-up");

        when(trainingService.getExerciseById(5)).thenReturn(ex);

        ResponseEntity<?> response = controller.getExerciseDetails(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Push-up", ((Exercise) response.getBody()).getName());
    }

    @Test
    void getTrainingDetails_shouldReturnOk() {
        TrainingDetailsResponse resp = new TrainingDetailsResponse();
        TrainingInfo info = new TrainingInfo();
        info.setId(10);
        info.setName("Strength");
        resp.setTrainingInfo(info);

        when(trainingService.getTrainingDetails(10)).thenReturn(resp);

        ResponseEntity<?> response = controller.getTrainingDetails(10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Strength", ((TrainingDetailsResponse) response.getBody()).getTrainingInfo().getName());
    }

    @Test
    void getUserTraining_shouldReturnOk() {
        TrainingInfo info = new TrainingInfo();
        info.setId(20);
        info.setName("Yoga");

        when(trainingService.getUserTraining(1)).thenReturn(info);

        ResponseEntity<?> response = controller.getUserTraining("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Yoga", ((TrainingInfo) response.getBody()).getName());
    }

    @Test
    void getUserTrainingWithDetails_shouldReturnOk() {
        TrainingInfo info = new TrainingInfo();
        info.setId(30);
        info.setName("Pilates");

        TrainingDetailsResponse resp = new TrainingDetailsResponse();
        resp.setTrainingInfo(info);

        when(trainingService.getUserTraining(1)).thenReturn(info);
        when(trainingService.getTrainingDetails(30)).thenReturn(resp);

        ResponseEntity<?> response = controller.getUserTrainingWithDetails("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pilates", ((TrainingDetailsResponse) response.getBody()).getTrainingInfo().getName());
    }

    @Test
    void assignTraining_shouldReturnOk() {
        doNothing().when(trainingService).assignTrainingToUser(1, 40);

        ResponseEntity<String> response = controller.assignTraining(40, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Training successfully assigned to user", response.getBody());
    }

    @Test
    void depriveTrainingFromUser_shouldReturnOk() {
        doNothing().when(trainingService).depriveTrainingFromUser(1);

        ResponseEntity<String> response = controller.depriveTrainingFromUser("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Training successfully deprived", response.getBody());
    }
}
