package study.snacktrack.ServiceTest;

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

class MealServiceTest {

    private UserRepository userRepository;
    private MealRepository mealRepository;
    private IngredientRepository ingredientRepository;
    private FoodRepository foodRepository;
    private FoodService foodService;
    private FavouriteRepository favouriteRepository;
    private MealService mealService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        mealRepository = mock(MealRepository.class);
        ingredientRepository = mock(IngredientRepository.class);
        foodRepository = mock(FoodRepository.class);
        foodService = mock(FoodService.class);
        favouriteRepository = mock(FavouriteRepository.class);

        mealService = new MealService(userRepository, foodRepository, mealRepository,
                ingredientRepository, foodService, favouriteRepository);
    }

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

    @Test
    void getAllMeals_shouldReturnList() {
        when(mealRepository.findAll()).thenReturn(List.of(new Meal(1, "Pizza", "Cheese")));
        List<Meal> result = mealService.getAllMeals();
        assertEquals(1, result.size());
    }

    @Test
    void getMealsByUser_shouldReturnList() {
        when(mealRepository.findByAuthorId(10)).thenReturn(List.of(new Meal(10, "Pizza", "Cheese")));
        List<Meal> result = mealService.getMealsByUser(10);
        assertEquals(1, result.size());
    }

    @Test
    void searchMealsByName_shouldReturnList() {
        when(mealRepository.findByNameContainingIgnoreCase("Pizza"))
                .thenReturn(List.of(new Meal(10, "Pizza", "Cheese")));
        List<Meal> result = mealService.searchMealsByName("Pizza");
        assertEquals(1, result.size());
    }

    @Test
    void getMealById_shouldReturnMeal() {
        Meal meal = new Meal(10, "Pizza", "Cheese");
        meal.setId(11);
        when(mealRepository.findById(11)).thenReturn(Optional.of(meal));

        Meal result = mealService.getMealById(11);

        assertEquals(11, result.getId());
    }

    @Test
    void deleteMealAsAdmin_shouldDeleteMeal() {
        Meal meal = new Meal(10, "Pizza", "Cheese");
        meal.setId(12);
        when(mealRepository.findById(12)).thenReturn(Optional.of(meal));

        mealService.deleteMealAsAdmin(12);

        verify(mealRepository).delete(meal);
    }
}