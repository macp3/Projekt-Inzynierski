package study.snacktrack.serviceTest;

import org.springframework.test.util.ReflectionTestUtils;
import study.snacktrack.repositories.*;
import study.snacktrack.entities.*;
import study.snacktrack.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class DailyCheckServiceTest {

    private UserRepository userRepository;
    private BodyParametersRepository bodyParametersRepository;
    private RegisteredAlimentationRepository alimentationRepository;
    private UserService userService;
    private FoodService foodService;
    private DailyCheckService dailyCheckService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        bodyParametersRepository = mock(BodyParametersRepository.class);
        alimentationRepository = mock(RegisteredAlimentationRepository.class);
        userService = mock(UserService.class);
        foodService = mock(FoodService.class);

        dailyCheckService = new DailyCheckService();
        
        ReflectionTestUtils.setField(dailyCheckService, "userRepository", userRepository);
        ReflectionTestUtils.setField(dailyCheckService, "bodyParametersRepository", bodyParametersRepository);
        ReflectionTestUtils.setField(dailyCheckService, "alimentationRepository", alimentationRepository);
        ReflectionTestUtils.setField(dailyCheckService, "userService", userService);
        ReflectionTestUtils.setField(dailyCheckService, "foodService", foodService);
    }

    @Test
    void checkCaloriesForAllUsers_shouldIncreaseStreakWhenWithinLimit() {
        User user = new User();
        user.setId(1);
        user.setStreak(2);

        BodyParameters params = new BodyParameters();
        params.setUserId(1);
        params.setCalorieLimit(2000f);

        RegisteredAlimentation entry = new RegisteredAlimentation();
        entry.setUserId(1);
        entry.setTimestamp(LocalDate.now().minusDays(1));

        EssentialFood food = new EssentialFood();
        food.setCalories(700);
        food.setDefaultWeight(100f);
        entry.setEssentialFood(food);
        entry.setAmount(200f);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(bodyParametersRepository.findByUserId(1)).thenReturn(Optional.of(params));
        when(alimentationRepository.findByUserIdAndTimestamp(eq(1), any(LocalDate.class)))
                .thenReturn(List.of(entry));

        dailyCheckService.checkCaloriesForAllUsers();

        verify(userService).updateStreak(1, 3);
    }

    @Test
    void checkCaloriesForAllUsers_shouldResetStreakWhenAboveLimit() {
        User user = new User();
        user.setId(2);
        user.setStreak(5);

        BodyParameters params = new BodyParameters();
        params.setUserId(2);
        params.setCalorieLimit(1500f);

        RegisteredAlimentation entry = new RegisteredAlimentation();
        entry.setUserId(2);
        entry.setTimestamp(LocalDate.now().minusDays(1));

        EssentialFood food = new EssentialFood();
        food.setCalories(1000);
        food.setDefaultWeight(100f);
        entry.setEssentialFood(food);
        entry.setAmount(200f); // 2000 kcal

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(bodyParametersRepository.findByUserId(2)).thenReturn(Optional.of(params));
        when(alimentationRepository.findByUserIdAndTimestamp(eq(2), any(LocalDate.class)))
                .thenReturn(List.of(entry));

        dailyCheckService.checkCaloriesForAllUsers();

        verify(userService).updateStreak(2, 0);
    }

    @Test
    void checkCaloriesForAllUsers_shouldSkipUserWithoutBodyParameters() {
        User user = new User();
        user.setId(3);
        user.setStreak(1);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(bodyParametersRepository.findByUserId(3)).thenReturn(Optional.empty());

        dailyCheckService.checkCaloriesForAllUsers();

        verify(userService, never()).updateStreak(anyInt(), anyInt());
    }
}
