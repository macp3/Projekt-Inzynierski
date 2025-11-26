package study.snacktrack.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import study.snacktrack.entities.*;
import study.snacktrack.dto.*;
import study.snacktrack.repositories.FoodRepository;
import study.snacktrack.services.FatSecretAuthService;
import study.snacktrack.services.FoodService;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodServiceTest {

    private FoodRepository foodRepository;
    private FatSecretAuthService authService;
    private FoodService foodService;

    @BeforeEach
    void setUp() {
        foodRepository = mock(FoodRepository.class);
        authService = mock(FatSecretAuthService.class);
        foodService = new FoodService(foodRepository, authService);
    }

    @Test
    void getAllEssentials_shouldReturnResponses() {
        EssentialFood food = new EssentialFood(
                1, "Apple", 10, "Fresh apple", 52, 0.3f, 0.2f, 14f,
                "piece", 100f, "Nature"
        );

        when(foodRepository.findAll()).thenReturn(List.of(food));

        List<EssentialFoodResponse> result = foodService.getAllEssentials();

        assertEquals(1, result.size());
        assertEquals("Apple", result.get(0).getName());
        assertEquals(52, result.get(0).getCalories());
    }

    @Test
    void addEssentialFood_shouldSaveFoodWhenValid() {
        User user = new User();
        user.setId(99);

        EssentialFoodRequest request = new EssentialFoodRequest(
                "Banana", "Yellow banana", 89, 1.1f, 0.3f, 23f,
                "BrandX", 120f, "piece"
        );

        when(foodRepository.existsByNameIgnoreCase("Banana")).thenReturn(false);

        String result = foodService.addEssentialFood(request, user);

        assertEquals("Food has been added to database", result);
        verify(foodRepository).save(any(EssentialFood.class));
    }

    @Test
    void addEssentialFood_shouldThrowWhenNameExists() {
        User user = new User();
        user.setId(99);

        EssentialFoodRequest request = new EssentialFoodRequest(
                "Banana", "Yellow banana", 89, 1.1f, 0.3f, 23f,
                "BrandX", 120f, "piece"
        );

        when(foodRepository.existsByNameIgnoreCase("Banana")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> foodService.addEssentialFood(request, user));
    }

    @Test
    void addEssentialFood_shouldThrowWhenCaloriesInvalid() {
        User user = new User();
        user.setId(99);

        EssentialFoodRequest request = new EssentialFoodRequest(
                "Orange", "Juicy orange", 0, 1.1f, 0.3f, 23f,
                "BrandY", 120f, "piece"
        );

        when(foodRepository.existsByNameIgnoreCase("Orange")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> foodService.addEssentialFood(request, user));
    }

    @Test
    void searchEssentialFood_shouldDelegateToRepository() {
        EssentialFood food = new EssentialFood(
                2, "Pear", 10, "Sweet pear", 57, 0.4f, 0.1f, 15f,
                "piece", 100f, "Nature"
        );

        when(foodRepository.findByNameContainingIgnoreCase("Pear"))
                .thenReturn(List.of(food));

        List<EssentialFood> result = foodService.searchEssentialFood("Pear");

        assertEquals(1, result.size());
        assertEquals("Pear", result.get(0).getName());
    }
}