package study.snacktrack.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import study.snacktrack.dto.IngredientRequest;
import study.snacktrack.dto.MealRequest;
import study.snacktrack.dto.MealResponse;
import study.snacktrack.entities.EssentialFood;
import study.snacktrack.entities.Ingredient;
import study.snacktrack.entities.Meal;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.*;
import study.snacktrack.services.FoodService;
import study.snacktrack.services.MealService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the MealService, covering the core business logic for meal creation, editing, deletion, and image uploading.
 * This class ensures that the service methods correctly handle validation, persist entities, and return appropriate responses.
 */
class MealServiceTest {

    private UserRepository userRepository;
    private MealRepository mealRepository;
    private IngredientRepository ingredientRepository;
    private FoodRepository foodRepository;
    private FoodService foodService;
    private MealService mealService;

    /**
     * Sets up mock repositories and services, and initializes the MealService instance before each test.
     * This isolates the service logic, allowing for verification of correct repository method calls.
     */
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        mealRepository = mock(MealRepository.class);
        ingredientRepository = mock(IngredientRepository.class);
        foodRepository = mock(FoodRepository.class);
        foodService = mock(FoodService.class);

        mealService = new MealService(userRepository, foodRepository, mealRepository,
                ingredientRepository, foodService);
    }

    /**
     * Tests the successful creation of a new meal with valid ingredients.
     * It verifies that the service saves the meal and associated ingredients and returns the generated meal ID.
     */
    @Test
    void createMeal_shouldSaveMealWhenValid() {
        MealRequest request = new MealRequest();
        request.setName("Pizza");
        request.setDescription("Cheese pizza");

        IngredientRequest ir = new IngredientRequest();
        ir.setEssentialFoodId(1);
        ir.setAmount(100f);
        request.setIngredients(List.of(ir));

        when(userRepository.existsById(10)).thenReturn(true);
        EssentialFood food = new EssentialFood();
        food.setId(1);
        food.setName("Cheese");
        when(foodRepository.findById(1)).thenReturn(Optional.of(food));

        Meal meal = new Meal(10, "Pizza", "Cheese pizza");
        meal.setId(99);
        when(mealRepository.save(any(Meal.class))).thenAnswer(inv -> {
            Meal m = inv.getArgument(0);
            m.setId(99);
            return m;
        });

        Integer id = mealService.createMeal(request, 10);

        assertEquals(99, id);
        verify(mealRepository).save(any(Meal.class));
    }

    /**
     * Tests that meal creation fails if the request contains no ingredients.
     * It verifies that the service throws an {@code IllegalArgumentException} to enforce the business rule requiring at least one ingredient.
     */
    @Test
    void createMeal_shouldThrowWhenNoIngredients() {
        MealRequest request = new MealRequest();
        request.setName("Pizza");
        request.setDescription("Cheese pizza");
        request.setIngredients(List.of());

        when(userRepository.existsById(10)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> mealService.createMeal(request, 10));
    }

    /**
     * Tests the successful upload of an image for an existing meal.
     * It verifies that the service updates the meal's image URL with the generated path and saves the entity.
     * @throws IOException if the mock file stream fails
     */
    @Test
    void uploadMealImage_shouldSaveImageUrl() throws IOException {
        Meal meal = new Meal(10, "Pizza", "Cheese pizza");
        meal.setId(5);
        when(mealRepository.findById(5)).thenReturn(Optional.of(meal));

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("img".getBytes()));

        String result = mealService.uploadMealImage(5, file, 10);

        assertTrue(result.contains("meal_5.jpg"));
        verify(mealRepository).save(meal);
    }

    /**
     * Tests the retrieval of a meal with its complete list of ingredients.
     * It verifies that the service fetches the meal and its related ingredients, mapping them correctly to the {@code MealResponse} DTO.
     */
    @Test
    void getMealWithIngredients_shouldReturnResponse() {
        Meal meal = new Meal(10, "Pizza", "Cheese pizza");
        meal.setId(7);
        when(mealRepository.findById(7)).thenReturn(Optional.of(meal));

        Ingredient ingredient = new Ingredient(meal, null, null, 100f, null);
        ingredient.setId(1);
        when(ingredientRepository.findByMealId(7)).thenReturn(List.of(ingredient));

        MealResponse response = mealService.getMealWithIngredients(7);

        assertEquals(7, response.getId());
        assertEquals("Pizza", response.getName());
        assertEquals(1, response.getIngredients().size());
    }

    /**
     * Tests the successful editing of a meal by its authorized user.
     * It verifies that the service updates the meal properties according to the request and saves the modified entity.
     */
    @Test
    void editMealByUser_shouldUpdateMeal() {
        Meal meal = new Meal(10, "Pizza", "Cheese pizza");
        meal.setId(8);
        when(mealRepository.findById(8)).thenReturn(Optional.of(meal));
        when(userRepository.existsById(10)).thenReturn(true);

        MealRequest request = new MealRequest();
        request.setName("Updated Pizza");
        request.setDescription("Updated desc");

        String result = mealService.editMealByUser(8, request, 10);

        assertEquals("Meal updated successfully", result);
        assertEquals("Updated Pizza", meal.getName());
        assertEquals("Updated desc", meal.getDescription());
        verify(mealRepository).save(meal);
    }

    /**
     * Tests the successful deletion of a meal by its authorized user.
     * It verifies that the service finds the correct meal and performs the delete operation.
     */
    @Test
    void deleteMealByUser_shouldDeleteWhenAuthorized() {
        User user = new User();
        user.setId(10);
        Meal meal = new Meal(10, "Pizza", "Cheese pizza");
        meal.setId(9);

        when(userRepository.findById(10)).thenReturn(Optional.of(user));
        when(mealRepository.findById(9)).thenReturn(Optional.of(meal));

        String result = mealService.deleteMealByUser(9, 10);

        assertEquals("Meal successfully deleted", result);
        verify(mealRepository).delete(meal);
    }

    /**
     * Tests the retrieval of all available meals.
     * It verifies that the service delegates the request to the repository and returns the list of meals.
     */
    @Test
    void getAllMeals_shouldReturnList() {
        when(mealRepository.findAll()).thenReturn(List.of(new Meal(1, "Pizza", "Cheese")));
        List<Meal> result = mealService.getAllMeals();
        assertEquals(1, result.size());
    }

    /**
     * Tests the retrieval of all meals created by a specific user ID.
     * It verifies that the service calls the repository method to filter meals by author ID.
     */
    @Test
    void getMealsByUser_shouldReturnList() {
        when(mealRepository.findByAuthorId(10)).thenReturn(List.of(new Meal(10, "Pizza", "Cheese")));
        List<Meal> result = mealService.getMealsByUser(10);
        assertEquals(1, result.size());
    }

    /**
     * Tests the search functionality for meals by name.
     * It verifies that the service delegates the search to the repository method that performs case-insensitive containment matching.
     */
    @Test
    void searchMealsByName_shouldReturnList() {
        when(mealRepository.findByNameContainingIgnoreCase("Pizza"))
                .thenReturn(List.of(new Meal(10, "Pizza", "Cheese")));
        List<Meal> result = mealService.searchMealsByName("Pizza");
        assertEquals(1, result.size());
    }

    /**
     * Tests the retrieval of a single meal by its ID.
     * It verifies that the service returns the correct {@code Meal} entity when found.
     */
    @Test
    void getMealById_shouldReturnMeal() {
        Meal meal = new Meal(10, "Pizza", "Cheese");
        meal.setId(11);
        when(mealRepository.findById(11)).thenReturn(Optional.of(meal));

        Meal result = mealService.getMealById(11);

        assertEquals(11, result.getId());
    }

    /**
     * Tests the functionality for an administrator to delete any meal.
     * It verifies that the service retrieves the meal by ID and unconditionally performs the deletion.
     */
    @Test
    void deleteMealAsAdmin_shouldDeleteMeal() {
        Meal meal = new Meal(10, "Pizza", "Cheese");
        meal.setId(12);
        when(mealRepository.findById(12)).thenReturn(Optional.of(meal));

        mealService.deleteMealAsAdmin(12);

        verify(mealRepository).delete(meal);
    }
}