package study.snacktrack.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import study.snacktrack.dto.RegisteredAlimentationRequest;
import study.snacktrack.dto.RegisteredAlimentationResponse;
import study.snacktrack.entities.enums.MealNames;
import study.snacktrack.repositories.*;
import study.snacktrack.services.*;
import study.snacktrack.entities.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisteredAlimentationServiceTest {

    private UserRepository userRepository;
    private FoodRepository foodRepository;
    private MealRepository mealRepository;
    private RegisteredAlimentationRepository repository;
    private JwtService jwtService;
    private FoodService foodService;
    private MealService mealService;
    private RegisteredAlimentationService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        foodRepository = mock(FoodRepository.class);
        mealRepository = mock(MealRepository.class);
        repository = mock(RegisteredAlimentationRepository.class);
        jwtService = mock(JwtService.class);
        foodService = mock(FoodService.class);
        mealService = mock(MealService.class);

        service = new RegisteredAlimentationService(
                userRepository, foodRepository, mealRepository,
                repository, jwtService, foodService, mealService);
    }

    @Test
    void addEntry_shouldSaveEssentialFoodEntry() {
        User user = new User();
        user.setId(1);
        when(jwtService.extractEmail(anyString())).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        EssentialFood food = new EssentialFood();
        food.setId(10);
        food.setName("Apple");
        when(foodRepository.findById(10)).thenReturn(Optional.of(food));

        RegisteredAlimentationRequest req = new RegisteredAlimentationRequest();
        req.setEssentialId(10);
        req.setAmount(100f);
        req.setMealName(MealNames.breakfast);

        String result = service.addEntry("Bearer token", req, null);

        assertEquals("Meal registered", result);
        verify(repository).save(any(RegisteredAlimentation.class));
    }

    @Test
    void addEntry_shouldThrowWhenNoAmountOrPieces() {
        User user = new User();
        user.setId(1);
        when(jwtService.extractEmail(anyString())).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        RegisteredAlimentationRequest req = new RegisteredAlimentationRequest();
        req.setEssentialId(10);
        req.setMealName(MealNames.lunch);

        assertThrows(IllegalArgumentException.class,
                () -> service.addEntry("Bearer token", req, null));
    }

    @Test
    void getMyEntries_shouldReturnResponses() {
        User user = new User();
        user.setId(1);
        when(jwtService.extractEmail(anyString())).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        RegisteredAlimentation entry = new RegisteredAlimentation();
        entry.setId(5);
        entry.setUserId(1);
        entry.setTimestamp(LocalDate.now());
        entry.setMealName(MealNames.snack);
        entry.setAmount(50f);

        when(repository.findByUserId(1)).thenReturn(List.of(entry));

        List<RegisteredAlimentationResponse> result = service.getMyEntries("Bearer token", null);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getId());
        assertEquals(MealNames.snack, result.get(0).getMealName());
    }

    @Test
    void deleteEntry_shouldRemoveEntryWhenOwner() {
        User user = new User();
        user.setId(1);
        when(jwtService.extractEmail(anyString())).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        RegisteredAlimentation entry = new RegisteredAlimentation();
        entry.setId(10);
        entry.setUserId(1);

        when(repository.findById(10)).thenReturn(Optional.of(entry));

        service.deleteEntry("Bearer token", 10);

        verify(repository).delete(entry);
    }

    @Test
    void deleteEntry_shouldThrowWhenNotOwner() {
        User user = new User();
        user.setId(1);
        when(jwtService.extractEmail(anyString())).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        RegisteredAlimentation entry = new RegisteredAlimentation();
        entry.setId(10);
        entry.setUserId(2);

        when(repository.findById(10)).thenReturn(Optional.of(entry));

        assertThrows(ResponseStatusException.class,
                () -> service.deleteEntry("Bearer token", 10));
    }

    @Test
    void updateEntry_shouldUpdateAmount() {
        User user = new User();
        user.setId(1);
        when(jwtService.extractEmail(anyString())).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        RegisteredAlimentation entry = new RegisteredAlimentation();
        entry.setId(10);
        entry.setUserId(1);

        when(repository.findById(10)).thenReturn(Optional.of(entry));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RegisteredAlimentationRequest req = new RegisteredAlimentationRequest();
        req.setAmount(200f);
        req.setMealName(MealNames.dinner);

        RegisteredAlimentation updated = service.updateEntry("Bearer token", 10, req);

        assertEquals(200f, updated.getAmount());
        verify(repository).save(entry);
    }

    @Test
    void copyMeal_shouldCopyEntries() {
        User user = new User();
        user.setId(1);
        when(jwtService.extractEmail(anyString())).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        RegisteredAlimentation entry = new RegisteredAlimentation();
        entry.setId(10);
        entry.setUserId(1);
        entry.setMealName(MealNames.breakfast);
        entry.setTimestamp(LocalDate.now());

        when(repository.findByUserIdAndTimestampAndMealName(eq(1), any(LocalDate.class), eq(MealNames.breakfast)))
                .thenReturn(List.of(entry));

        String result = service.copyMeal("Bearer token",
                LocalDate.now().toString(), MealNames.breakfast,
                LocalDate.now().plusDays(1).toString(), MealNames.lunch);

        assertTrue(result.contains("Meal copied successfully"));
        verify(repository, atLeastOnce()).save(any(RegisteredAlimentation.class));
    }
}