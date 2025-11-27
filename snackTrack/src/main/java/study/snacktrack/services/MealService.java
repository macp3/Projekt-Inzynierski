package study.snacktrack.services;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import study.snacktrack.dto.*;
import study.snacktrack.entities.*;
import study.snacktrack.repositories.*;

/**
 * Service responsible for managing meals and their ingredients.
 * Provides functionality for creating, editing, deleting, and retrieving meals.
 */
@Service
public class MealService {

    @Value("${image.upload-dir:src/main/resources/static/images/meals}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final IngredientRepository ingredientRepository;
    private final FoodRepository foodRepository;
    private final FoodService foodService;

    /**
     * Constructs MealService with required repositories and services.
     */
    public MealService(UserRepository userRepository, FoodRepository foodRepository, MealRepository mealRepository,
                       IngredientRepository ingredientRepository, FoodService foodService) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.ingredientRepository = ingredientRepository;
        this.foodRepository = foodRepository;
        this.foodService = foodService;
    }

    /**
     * Validates if a meal exists by ID.
     *
     * @param mealId meal identifier
     * @return Meal entity if found
     */
    private Meal validateMealExistance(int mealId) {
        if (mealId <= 0) {
            throw new IllegalArgumentException("Meal ID must be greater than zero");
        }

        Optional<Meal> optionalMeal = mealRepository.findById(mealId);

        if (optionalMeal.isEmpty()) {
            throw new IllegalArgumentException("Meal with specified ID doesn't exist");
        }

        return optionalMeal.get();
    }

    /**
     * Creates a new meal with ingredients.
     *
     * @param request meal request data
     * @param userId author identifier
     * @return created meal ID
     */
    @Transactional
    public Integer createMeal(MealRequest request, int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with this ID doesn't exist");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Meal's name must not be empty");
        }

        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description must not be empty");
        }

        if (request.getIngredients().isEmpty()) {
            throw new IllegalArgumentException("The meal has to have at least one ingredient");
        }

        Meal meal = new Meal(userId, request.getName(), request.getDescription());

        for (IngredientRequest ir : request.getIngredients()) {
            EssentialFood essentialFood = null;
            Integer essentialApiId = null;
            Float amount = null;
            Float pieces = null;

            if (ir.getEssentialFoodId() != null) {
                essentialFood = foodRepository.findById(ir.getEssentialFoodId())
                        .orElseThrow(() -> new IllegalArgumentException("Food not found"));
            } else if (ir.getEssentialApiId() != null) {
                essentialApiId = ir.getEssentialApiId();
            } else {
                throw new IllegalArgumentException("Food must have either essential id or API id");
            }

            if (ir.getAmount() != null) {
                amount = ir.getAmount();
            } else if (ir.getPieces() != null) {
                pieces = ir.getPieces();
            } else {
                throw new IllegalArgumentException("Food must have either pieces or amount");
            }

            Ingredient ingredient = new Ingredient(meal, essentialFood, essentialApiId, amount, pieces);
            meal.addIngredient(ingredient);
        }

        mealRepository.save(meal);
        return meal.getId();
    }

    /**
     * Uploads or replaces a meal image.
     *
     * @param mealId meal identifier
     * @param imageFile image file
     * @param userId author identifier
     * @return relative path to uploaded image
     */
    @Transactional
    public String uploadMealImage(int mealId, MultipartFile imageFile, Integer userId) throws IOException {
        Meal meal = validateMealExistance(mealId);

        if (!userId.equals(meal.getAuthorId())) {
            throw new AccessDeniedException("Only author can edit meal picture");
        }

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Path uploadPath = Paths.get("/app/uploads/meals");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = "meal_" + mealId + ".jpg";
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        long ts = System.currentTimeMillis();
        String relativePath = "/images/meals/" + fileName + "?t=" + ts;

        meal.setImageUrl(relativePath);
        mealRepository.save(meal);

        return relativePath;
    }

    /**
     * Retrieves a meal with its ingredients.
     *
     * @param mealId meal identifier
     * @return meal response with ingredients
     */
    public MealResponse getMealWithIngredients(int mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        List<Ingredient> ingredients = ingredientRepository.findByMealId(mealId);

        List<IngredientResponse> ingredientResponses = ingredients.stream()
                .map(ingredient -> {
                    IngredientResponse dto = new IngredientResponse();
                    dto.setId(ingredient.getId());
                    dto.setAmount(ingredient.getAmount());
                    dto.setPieces(ingredient.getPieces());

                    if (ingredient.getEssentialApiId() != null) {
                        dto.setEssentialApi(foodService.getFoodFromApiById(ingredient.getEssentialApiId()));
                    } else if (ingredient.getEssentialFood() != null) {
                        dto.setEssentialFood(new EssentialFoodResponse(ingredient.getEssentialFood()));
                    }

                    return dto;
                })
                .toList();

        MealResponse response = new MealResponse();
        response.setId(meal.getId());
        response.setName(meal.getName());
        response.setDescription(meal.getDescription());
        response.setAuthorId(meal.getAuthorId());
        response.setIngredients(ingredientResponses);
        response.setImageUrl(meal.getImageUrl());

        return response;
    }

    /**
     * Edits an existing meal by its author.
     *
     * @param mealId meal identifier
     * @param request updated meal data
     * @param userId author identifier
     * @return confirmation message
     */
    public String editMealByUser(int mealId, MealRequest request, int userId) {
        Meal meal = validateMealExistance(mealId);
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with this ID doesn't exist");
        }

        if (meal.getAuthorId() != userId) {
            throw new IllegalArgumentException("You are not allowed to edit this meal");
        }

        if (request.getName() != null) {
            if (request.getName().isBlank()) {
                throw new IllegalArgumentException("Meal's name must not be empty");
            }
            meal.setName(request.getName());
        }

        if (request.getDescription() != null) {
            if (request.getDescription().isBlank()) {
                throw new IllegalArgumentException("Meal's description must not be empty");
            }
            meal.setDescription(request.getDescription());
        }

        if (request.getIngredients() != null) {
            if (request.getIngredients().isEmpty()) {
                throw new IllegalArgumentException("The meal has to have at least 1 ingredient");
            }

            ingredientRepository.deleteAll(meal.getIngredients());
            meal.getIngredients().clear();

            for (IngredientRequest ir : request.getIngredients()) {
                EssentialFood essentialFood = null;
                Integer essentialApiId = null;
                Float amount = null;
                Float pieces = null;

                if (ir.getEssentialFoodId() != null) {
                    essentialFood = foodRepository.findById(ir.getEssentialFoodId())
                            .orElseThrow(() -> new IllegalArgumentException("Food not found"));
                } else if (ir.getEssentialApiId() != null) {
                    essentialApiId = ir.getEssentialApiId();
                } else {
                    throw new IllegalArgumentException("Food must have either essential id or API id");
                }

                if (ir.getAmount() != null) {
                    amount = ir.getAmount();
                } else if (ir.getPieces() != null) {
                    pieces = ir.getPieces();
                } else {
                    throw new IllegalArgumentException("Food must have either pieces or amount");
                }

                Ingredient ingredient = new Ingredient(meal, essentialFood, essentialApiId, amount, pieces);
                meal.addIngredient(ingredient);
            }
        }

        mealRepository.save(meal);
        return "Meal updated successfully";
    }

    /**
     * Deletes a meal created by a specific user.
     *
     * @param mealId meal identifier
     * @param userId user identifier
     * @return confirmation message
     */
    public String deleteMealByUser(int mealId, int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("There is no user with specified ID");
        }

        User user = optionalUser.get();
        Meal meal = validateMealExistance(mealId);

        if (user.getId() != meal.getAuthorId()) {
            throw new IllegalArgumentException("This user is not authorized to delete this meal");
        }

        mealRepository.delete(meal);
        return "Meal successfully deleted";
    }

    /**
     * Retrieves all meals from repository.
     *
     * @return list of all meals
     */
    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    /**
     * Retrieves all meals created by a specific user.
     *
     * @param userId user identifier
     * @return list of meals created by user
     */
    public List<Meal> getMealsByUser(int userId) {
        return mealRepository.findByAuthorId(userId);
    }

    /**
     * Searches meals by name.
     *
     * @param name meal name query
     * @return list of matching meals
     */
    public List<Meal> searchMealsByName(String name) {
        return mealRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Retrieves a meal by its ID.
     *
     * @param mealId meal identifier
     * @return Meal entity
     */
    public Meal getMealById(int mealId) {
        return mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("This meal doesn't exist"));
    }

    /**
     * Deletes a meal as an administrator.
     *
     * @param mealId meal identifier
     */
    public void deleteMealAsAdmin(int mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

        mealRepository.delete(meal);
    }
}
