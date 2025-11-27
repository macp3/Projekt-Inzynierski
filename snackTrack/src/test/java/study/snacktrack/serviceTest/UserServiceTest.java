package study.snacktrack.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import study.snacktrack.dto.BodyParametersResponse;
import study.snacktrack.entities.enums.Sex;
import study.snacktrack.entities.enums.Status;
import study.snacktrack.repositories.*;
import study.snacktrack.entities.*;
import study.snacktrack.services.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserService, covering administrative functions and user profile management, including password change, status updates, and body parameters modification.
 * This class ensures that the service enforces data integrity, uses the password encoder correctly, and properly interacts with various user-related repositories.
 */
class UserServiceTest {

    private UserRepository userRepository;
    private BodyParametersRepository bodyParametersRepository;
    private UserDeviceTokenRepository deviceTokenRepository;
    private FavouriteRepository favouriteRepository;
    private MealRepository mealRepository;
    private PasswordEncoder passwordEncoder;
    private UserService service;

    /**
     * Sets up mock repositories and services, and initializes the UserService instance before each test.
     * ReflectionTestUtils is used to inject the mocked {@code PasswordEncoder} into the private field of the service.
     */
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        bodyParametersRepository = mock(BodyParametersRepository.class);
        deviceTokenRepository = mock(UserDeviceTokenRepository.class);
        favouriteRepository = mock(FavouriteRepository.class);
        mealRepository = mock(MealRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        service = new UserService(userRepository, bodyParametersRepository,
                deviceTokenRepository, favouriteRepository, mealRepository);
        ReflectionTestUtils.setField(service, "passwordEncoder", passwordEncoder);
    }

    /**
     * Tests the functionality to change a user's password.
     * It verifies that the service uses the {@code PasswordEncoder} to hash the new password and saves the updated user entity.
     */
    @Test
    void changePassword_shouldUpdatePassword() {
        User user = new User();
        user.setId(1);
        user.setPassword("oldHash");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("newPass", "oldHash")).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("newHash");

        User updated = service.changePassword(1, "newPass");

        assertEquals("newHash", updated.getPassword());
        verify(userRepository).save(user);
    }

    /**
     * Tests the functionality to update a user's body parameters.
     * It verifies that the service calculates the calorie limit (implicitly) and saves the updated {@code BodyParameters} entity.
     */
    @Test
    void changeBodyParameters_shouldUpdateValues() {
        BodyParameters bp = new BodyParameters();
        bp.setUserId(1);
        bp.setSex(Sex.male);
        bp.setHeight(180f);
        bp.setWeight(80f);
        bp.setAge(25);
        bp.setDailyActivityFactor(1f);
        bp.setDailyActivityTrainingFactor(0.5f);
        bp.setWeeklyWeightChangeTempo(0.5f);
        bp.setGoalWeight(75f);

        when(bodyParametersRepository.findByUserId(1)).thenReturn(Optional.of(bp));
        when(bodyParametersRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BodyParametersResponse response = service.changeBodyParameters(1, Sex.male, 180f, 80f, 25,
                1f, 0.5f, 0.5f, 75f);

        assertEquals(1, response.getUserId());
        assertTrue(response.getCalorieLimit() > 0);
    }

    /**
     * Tests the functionality to update a user's daily streak count.
     * It verifies that the streak is correctly set and the user entity is persisted.
     */
    @Test
    void updateStreak_shouldSaveUser() {
        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        boolean result = service.updateStreak(1, 10);

        assertTrue(result);
        assertEquals(10, user.getStreak());
        verify(userRepository).save(user);
    }

    /**
     * Tests the functionality to update a user's status (e.g., from active to banned).
     * It verifies that the status field is changed and the updated user entity is saved.
     */
    @Test
    void updateStatus_shouldChangeStatus() {
        User user = new User();
        user.setId(1);
        user.setStatus(Status.active);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User updated = service.updateStatus(1, Status.banned);

        assertEquals(Status.banned, updated.getStatus());
        verify(userRepository).save(user);
    }

    /**
     * Tests the functionality to set a user's premium expiration date.
     * It verifies that the date is correctly parsed and set on the user entity.
     */
    @Test
    void updatePremiumExpiration_shouldSetDate() {
        User user = new User();
        user.setId(1);
        user.setStatus(Status.active);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User updated = service.updatePremiumExpiration(1, LocalDate.now().plusDays(1).toString());

        assertNotNull(updated.getPremiumExpiration());
    }

    /**
     * Tests the retrieval of a user entity by its ID.
     * It verifies that the service delegates the call to the repository and returns the correct user.
     */
    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = service.getUserById(1);

        assertEquals(1, result.getId());
    }

    /**
     * Tests the functionality to save a user's device token, verifying that an existing token is updated instead of creating a new one.
     * It ensures the service handles existing tokens by updating the value and saving the entity.
     */
    @Test
    void saveDeviceToken_shouldUpdateExisting() {
        UserDeviceToken token = new UserDeviceToken(1, "oldToken");
        when(deviceTokenRepository.findByUserId(1)).thenReturn(List.of(token));

        service.saveDeviceToken(1, "newToken");

        assertEquals("newToken", token.getDeviceToken());
        verify(deviceTokenRepository).save(token);
    }

    /**
     * Tests the retrieval of users based on a recipient type (e.g., premium users).
     * It verifies that the service calls the appropriate repository method to fetch users with an active premium subscription.
     */
    @Test
    void getUsersByRecipientType_shouldReturnPremium() {
        when(userRepository.findByPremiumExpirationNotNull()).thenReturn(List.of(new User()));

        List<User> result = service.getUsersByRecipientType("premium");

        assertEquals(1, result.size());
    }

    /**
     * Tests the upload of a user's profile image.
     * It verifies that the service generates the correct image path, saves the user entity, and handles the file.
     * @throws IOException if the mock file stream fails
     */
    @Test
    void uploadProfileImage_shouldSaveImage() throws IOException {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String path = service.uploadProfileImage(1, file);

        assertTrue(path.contains("profile_1.jpg"));
        verify(userRepository).save(user);
    }

    /**
     * Tests the functionality to add a meal to a user's favorites list.
     * It verifies that a new {@code Favourite} record is created and persisted when one does not already exist.
     */
    @Test
    void addFavourite_shouldSaveNewFavourite() {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(favouriteRepository.findByUserIdAndMealId(1, 2)).thenReturn(Optional.empty());

        Favourite fav = new Favourite();
        fav.setUserId(1);
        fav.setMealId(2);
        when(favouriteRepository.save(any())).thenReturn(fav);

        Favourite result = service.addFavourite(2, 1);

        assertEquals(2, result.getMealId());
        verify(favouriteRepository).save(any(Favourite.class));
    }

    /**
     * Tests the functionality to toggle a user's ban status.
     * It verifies that an active user is changed to banned status and the updated entity is saved.
     */
    @Test
    void toggleUserBan_shouldSwitchStatus() {
        User user = new User();
        user.setId(1);
        user.setStatus(Status.active);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        service.toggleUserBan(1);

        assertEquals(Status.banned, user.getStatus());
        verify(userRepository).save(user);
    }

    /**
     * Tests the retrieval of users as a paginated list.
     * It verifies that the service delegates the request to the repository's {@code findAll(Pageable)} method.
     */
    @Test
    void getUsersPage_shouldReturnPage() {
        Page<User> page = new PageImpl<>(List.of(new User()));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<User> result = service.getUsersPage(0, 10, null);

        assertEquals(1, result.getContent().size());
    }
}