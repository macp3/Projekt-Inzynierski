package study.snacktrack.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import study.snacktrack.controllers.UserController;
import study.snacktrack.dto.*;
import study.snacktrack.entities.*;
import study.snacktrack.entities.enums.Sex;
import study.snacktrack.repositories.BodyParametersRepository;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.services.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private JwtService jwtService;
    private NotificationService notificationService;
    private UserRepository userRepository;
    private BodyParametersRepository bodyParametersRepository;
    private MealService mealService;
    private UserController controller;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        jwtService = mock(JwtService.class);
        notificationService = mock(NotificationService.class);
        userRepository = mock(UserRepository.class);
        bodyParametersRepository = mock(BodyParametersRepository.class);
        mealService = mock(MealService.class);

        controller = new UserController(userService, jwtService, notificationService, userRepository, bodyParametersRepository, mealService);

        when(jwtService.extractEmail("token")).thenReturn("user@example.com");
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        user.setStreak(5);
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
    }

    @Test
    void getProfileInfo_shouldReturnUser() {
        ResponseEntity<?> response = controller.getProfileInfo("Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof User);
    }

    @Test
    void getUserId_shouldReturnId() {
        ResponseEntity<?> response = controller.getUserId("Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody());
    }

    @Test
    void changePassword_shouldReturnOk() {
        ResponseEntity<String> response = controller.changePassword("Bearer token", "newPass");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());
    }

    @Test
    void changeBodyParameters_shouldReturnResponse() {
        BodyParametersRequest req = new BodyParametersRequest(Sex.male, 180f, 75f, 25, 1.2f, 1.1f, 0.5f, 70f);
        BodyParametersResponse resp = new BodyParametersResponse(1, Sex.male, 180f, 75f, 25, 1.2f, 1.1f, 0.5f, 70f, 2000f, 150f, 70f, 250f);
        when(userService.changeBodyParameters(anyInt(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(resp);

        ResponseEntity<?> response = controller.changeBodyParameters("Bearer token", req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resp, response.getBody());
    }

    @Test
    void addBodyParameters_shouldReturnEntity() {
        BodyParametersRequest req = new BodyParametersRequest(Sex.male, 180f, 75f, 25, 1.2f, 1.1f, 0.5f, 70f);
        BodyParameters bp = new BodyParameters();

        when(userService.addBodyParameters(anyInt(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(bp);

        ResponseEntity<?> response = controller.addBodyParameters("Bearer token", req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bp, response.getBody());
    }

    @Test
    void refreshSurvey_shouldReturnLoginResponse() {
        when(bodyParametersRepository.existsByUserId(1)).thenReturn(false);
        ResponseEntity<?> response = controller.refreshSurvey("Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
    }

    @Test
    void getMyStreak_shouldReturnInt() {
        ResponseEntity<?> response = controller.getMyStreak("Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody());
    }

    @Test
    void getUserNotifications_shouldReturnList() {
        NotificationResponse nr = new NotificationResponse(1, "Test", "Desc", LocalDate.now());
        when(notificationService.getNotificationsByUser(1)).thenReturn(List.of(nr));

        ResponseEntity<?> response = controller.getUserNotifications("Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void saveDeviceToken_shouldReturnOk() {
        doNothing().when(userService).saveDeviceToken(1, "devToken");
        ResponseEntity<String> response = controller.saveDeviceToken("Bearer token", Map.of("token", "devToken"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Device token saved", response.getBody());
    }

    @Test
    void addFavourite_shouldReturnFavourite() {
        Meal meal = new Meal();
        meal.setId(10);
        Favourite fav = new Favourite();
        fav.setId(1);
        fav.setMealId(10);
        when(mealService.getMealById(10)).thenReturn(meal);
        when(userService.addFavourite(10, 1)).thenReturn(fav);

        ResponseEntity<?> response = controller.addFavourite(10, "Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fav, response.getBody());
    }

    @Test
    void removeFavourite_shouldReturnOk() {
        Meal meal = new Meal();
        meal.setId(10);
        when(mealService.getMealById(10)).thenReturn(meal);
        doNothing().when(userService).removeFavourite(10, 1);

        ResponseEntity<String> response = controller.removeFavourite(10, "Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Favourite removed successfully", response.getBody());
    }

    @Test
    void getMyFavouriteMeals_shouldReturnList() {
        Meal meal = new Meal();
        meal.setId(10);
        when(userService.getMyFavouriteMeals(1)).thenReturn(List.of(meal));

        ResponseEntity<?> response = controller.getMyFavouriteMeals("Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) response.getBody()).size());
    }

    @Test
    void uploadProfileImage_shouldReturnUrl() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(userService.uploadProfileImage(1, file)).thenReturn("/images/profile.jpg");

        ResponseEntity<String> response = controller.uploadProfileImage("Bearer token", file);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("profile.jpg"));
    }

    @Test
    void getBodyParameters_shouldReturnResponse() {
        BodyParametersResponse resp = new BodyParametersResponse(1, Sex.male, 180f, 75f, 25, 1.2f, 1.1f, 0.5f, 70f, 2000f, 150f, 70f, 250f);
        when(userService.getUserBodyParametersResponse(1)).thenReturn(resp);

        ResponseEntity<?> response = controller.getBodyParameters("Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resp, response.getBody());
    }

    @Test
    void updatePremium_shouldReturnOk() {
        User updated = new User();
        updated.setId(1);
        when(userService.updatePremiumExpiration(1, "2025-12-31")).thenReturn(updated);

        ResponseEntity<?> response = controller.updatePremium("Bearer token", "2025-12-31");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Premium updated", response.getBody());
    }
}
