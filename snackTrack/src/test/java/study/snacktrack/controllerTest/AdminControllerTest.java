package study.snacktrack.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import study.snacktrack.controllers.AdminController;
import study.snacktrack.dto.DashboardStatsResponse;
import study.snacktrack.dto.TrainingRequest;
import study.snacktrack.entities.Admin;
import study.snacktrack.entities.User;
import study.snacktrack.entities.enums.Status;
import study.snacktrack.repositories.AdminRepository;
import study.snacktrack.repositories.ExerciseRepository;
import study.snacktrack.repositories.TrainingInfoRepository;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.services.JwtService;
import study.snacktrack.services.TrainingService;
import study.snacktrack.services.UserService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserRepository userRepository;
    @Mock
    private TrainingInfoRepository trainingInfoRepository;
    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private UserService userService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private JwtService jwtService;

    private final int MOCK_USER_ID = 1;
    private final String MOCK_EMAIL = "admin@test.pl";
    private final String MOCK_TOKEN = "mock.jwt.token";
    private final String AUTH_HEADER = "Bearer " + MOCK_TOKEN;

    private User mockUser;
    private Admin mockAdmin;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(MOCK_USER_ID);
        mockUser.setStatus(Status.active);

        mockAdmin = new Admin();
        mockAdmin.setId(100);
        mockAdmin.setEmail(MOCK_EMAIL);
    }

    @Test
    void getDashboardStats_ReturnsCorrectStats() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByStatus(Status.active)).thenReturn(80L);
        when(userRepository.countByStatus(Status.banned)).thenReturn(5L);
        when(userRepository.countByPremiumExpirationAfter(any(LocalDate.class))).thenReturn(15L);
        when(trainingInfoRepository.count()).thenReturn(50L);
        when(exerciseRepository.count()).thenReturn(120L);

        ResponseEntity<DashboardStatsResponse> response = adminController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(userRepository).count();
    }

    @Test
    void getUserInfo_UserExists_ReturnsUser() {
        when(userService.getUserById(MOCK_USER_ID)).thenReturn(mockUser);

        ResponseEntity<?> response = adminController.getUserInfo(MOCK_USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).getUserById(MOCK_USER_ID);
    }

    @Test
    void toggleUserBan_Success_ReturnsOk() {
        doNothing().when(userService).toggleUserBan(MOCK_USER_ID);

        ResponseEntity<?> response = adminController.toggleUserBan(MOCK_USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).toggleUserBan(MOCK_USER_ID);
    }

    @Test
    void getAllUsers_ReturnsPaginatedList() {
        Page<User> mockPage = new PageImpl<>(Collections.singletonList(mockUser));
        when(userService.getUsersPage(0, 25, null)).thenReturn(mockPage);

        ResponseEntity<?> response = adminController.getAllUsers(0, 25, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPage, response.getBody());
    }

    @Test
    void addTraining_Success_ReturnsOk() {
        TrainingRequest.TreningInfo info = new TrainingRequest.TreningInfo();
        info.setName("Test Training");
        info.setDescription("Opis testowy");

        TrainingRequest mockRequest = new TrainingRequest();
        mockRequest.setTreningInfo(info);
        mockRequest.setTrainingExercises(Collections.emptyList());

        when(jwtService.extractEmail(MOCK_TOKEN)).thenReturn(MOCK_EMAIL);
        when(adminRepository.findByEmail(MOCK_EMAIL)).thenReturn(Optional.of(mockAdmin));
        doNothing().when(trainingService).createTraining(any(TrainingRequest.class), eq(mockAdmin.getId()));

        ResponseEntity<String> response = adminController.addTraining(mockRequest, AUTH_HEADER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Training added successfully", response.getBody());

        verify(jwtService).extractEmail(MOCK_TOKEN);
        verify(adminRepository).findByEmail(MOCK_EMAIL);
        verify(trainingService).createTraining(eq(mockRequest), eq(mockAdmin.getId()));
    }

    @Test
    void deleteTraining_Success_ReturnsOk() {
        int trainingId = 5;
        doNothing().when(trainingService).deleteTraining(trainingId);

        ResponseEntity<String> response = adminController.deleteTraining(trainingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Training deleted successfully", response.getBody());
        verify(trainingService).deleteTraining(trainingId);
    }
}
