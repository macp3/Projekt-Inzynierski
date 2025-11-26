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

class UserServiceTest {

    private UserRepository userRepository;
    private BodyParametersRepository bodyParametersRepository;
    private UserDeviceTokenRepository deviceTokenRepository;
    private FavouriteRepository favouriteRepository;
    private MealRepository mealRepository;
    private PasswordEncoder passwordEncoder;
    private UserService service;

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

    @Test
    void updatePremiumExpiration_shouldSetDate() {
        User user = new User();
        user.setId(1);
        user.setStatus(Status.active);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User updated = service.updatePremiumExpiration(1, LocalDate.now().plusDays(1).toString());

        assertNotNull(updated.getPremiumExpiration());
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = service.getUserById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void saveDeviceToken_shouldUpdateExisting() {
        UserDeviceToken token = new UserDeviceToken(1, "oldToken");
        when(deviceTokenRepository.findByUserId(1)).thenReturn(List.of(token));

        service.saveDeviceToken(1, "newToken");

        assertEquals("newToken", token.getDeviceToken());
        verify(deviceTokenRepository).save(token);
    }

    @Test
    void getUsersByRecipientType_shouldReturnPremium() {
        when(userRepository.findByPremiumExpirationNotNull()).thenReturn(List.of(new User()));

        List<User> result = service.getUsersByRecipientType("premium");

        assertEquals(1, result.size());
    }

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

    @Test
    void getUsersPage_shouldReturnPage() {
        Page<User> page = new PageImpl<>(List.of(new User()));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<User> result = service.getUsersPage(0, 10, null);

        assertEquals(1, result.getContent().size());
    }
}