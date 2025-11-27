package study.snacktrack.serviceTest;

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

/**
 * Unit tests for the RegisteredAlimentationService, covering the core business logic for logging food entries, retrieving, updating, and deleting records.
 * This class ensures that the service correctly handles authentication, validation, and data manipulation for user's daily dietary log.
 */
class RegisteredAlimentationServiceTest {

    private UserRepository userRepository;
    private FoodRepository foodRepository;
    private MealRepository mealRepository;
    private RegisteredAlimentationRepository repository;
    private JwtService jwtService;
    private FoodService foodService;
    private MealService mealService;
    private RegisteredAlimentationService service;

    /**
     * Sets up mock repositories and services, and initializes the RegisteredAlimentationService instance before each test.
     * This isolates the service logic, allowing for focused testing of interactions and business rules.
     */
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

    /**
     * Tests the successful addition of a new food entry based on an essential food ID and amount.
     * It verifies that the service successfully resolves the user and food, and saves the new {@code RegisteredAlimentation} entity.
     */
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

    /**
     * Tests that adding a food entry throws an exception if neither amount nor pieces are provided in the request.
     * It verifies the service enforces the validation rule that at least one quantity metric must be present.
     */
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

    /**
     * Tests the retrieval of all registered alimentation entries for the authenticated user.
     * It verifies that the service fetches the entries by user ID and maps them correctly to {@code RegisteredAlimentationResponse} DTOs.
     */
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

    /**
     * Tests the successful deletion of an alimentation entry by its owner.
     * It verifies that the service checks for ownership and performs the delete operation if the user is authorized.
     */
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

    /**
     * Tests that deleting an alimentation entry fails if the authenticated user is not the owner.
     * It verifies that the service throws a {@code ResponseStatusException} (403 Forbidden) when unauthorized access is attempted.
     */
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

    /**
     * Tests the successful update of an existing alimentation entry's amount and meal name.
     * It verifies that the service finds the entry, updates its properties, and saves the modified entity.
     */
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

    /**
     * Tests the functionality to copy all entries from one meal time/date to another.
     * It verifies that the service fetches the source entries, clones them with new meal time and date stamps, and saves the new list.
     */
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

        LocalDate today = LocalDate.now();

        when(repository.findByUserIdAndTimestampAndMealName(eq(1), any(LocalDate.class), eq(MealNames.breakfast)))
                .thenReturn(List.of(entry));

        String result = service.copyMeal("Bearer token",
                today.toString(), MealNames.breakfast,
                today.plusDays(1).toString(), MealNames.lunch);

        assertTrue(result.contains("Copied 1 items successfully"));

        verify(repository).saveAll(anyList());
    }
}