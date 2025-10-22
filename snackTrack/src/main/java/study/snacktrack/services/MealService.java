package study.snacktrack.services;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import study.snacktrack.dto.*;
import study.snacktrack.entities.*;
import study.snacktrack.repositories.*;

@Service
public class MealService {

    @Value("${image.upload-dir:src/main/resources/static/images/meals}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final IngredientRepository ingredientRepository;
    private final FoodRepository foodRepository;
    private final FoodService foodService;
    private final MealDietTypeRepository mealDietTypeRepository;
    private final FavouriteRepository favouriteRepository;
    private final DietTypeRepository dietTypeRepository;

    public MealService(UserRepository userRepository, FoodRepository foodRepository, MealRepository mealRepository,
            IngredientRepository ingredientRepository, FoodService foodService,
            MealDietTypeRepository mealDietTypeRepository, FavouriteRepository favouriteRepository,
            DietTypeRepository dietTypeRepository) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.ingredientRepository = ingredientRepository;
        this.foodRepository = foodRepository;
        this.foodService = foodService;
        this.mealDietTypeRepository = mealDietTypeRepository;
        this.favouriteRepository = favouriteRepository;
        this.dietTypeRepository = dietTypeRepository;
    }

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

    // o to jest giga kociol - naucz sie :)
    @Transactional
    public String createMeal(MealRequest request, int userId) {
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
            Integer pieces = null;

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
        return "Meal has been created";
    }

    // do naprawy = sprawdzic autora bo tylko autor moze dodawac zdjecie do swojego
    // meala
    // i przetestuj
    // dziala przetestowane
    @Transactional
    public String uploadMealImage(int mealId, MultipartFile imageFile, Integer userId) throws IOException {
        Meal meal = validateMealExistance(mealId);

        if (!userId.equals(meal.getAuthorId())) {
            throw new AccessDeniedException("Only author can edit meal picture");
        }

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "images", "meals");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File saved to: " + filePath.toAbsolutePath());

        String relativePath = "/uploads/images/meals/" + fileName;
        meal.setImageUrl(relativePath);
        mealRepository.save(meal);

        return relativePath;
    }

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
                        System.out.println(foodService.getFoodFromApiById(ingredient.getEssentialApiId()));
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

    // edit meal
    // jezeli w request nie sa podane pojedyncze pola to po prostu ich nie zmienia
    // (zostawia stare) - jezeli nie chcemy edytowac meal name to nie dodajemy do
    // requesta w jsonie "name": *to samo*
    // aktualizacja pol jezeli podano: zrobione
    public String editMealByUser(int mealId, MealRequest request, int userId) {
        Meal meal = validateMealExistance(mealId);
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with this ID doesn't exist");
        }

        if (meal.getAuthorId() != userId) {
            throw new IllegalArgumentException("You can't edit this meal");
        }

        if (userId != meal.getAuthorId()) {
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
                Integer pieces = null;

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

    // delete meal
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

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public List<Meal> getMealsByUser(int userId) {
        return mealRepository.findByAuthorId(userId);
    }

    public List<Meal> searchMealsByName(String name) {
        return mealRepository.findByNameContainingIgnoreCase(name);
    }

    public Meal getMealById(int mealId) {
        return mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("This meal doesn't exist"));
    }

    @Transactional
    public void assignDietTypesToMeal(int mealId, List<Integer> dietTypeIds) {
        if (!mealRepository.existsById(mealId)) {
            throw new IllegalArgumentException("Meal not found with id: " + mealId);
        }

        // dodalem
        if (dietTypeIds == null)
            throw new IllegalArgumentException("Diet types must not be null");

        mealDietTypeRepository.deleteByMealId(mealId);

        for (Integer dietTypeId : dietTypeIds) {
            if (!dietTypeRepository.existsById(dietTypeId)) {
                throw new IllegalArgumentException("DietType not found with id: " + dietTypeId);
            }

            MealDietType mdt = new MealDietType();
            mdt.setMealId(mealId);
            mdt.setDietTypeId(dietTypeId);
            mealDietTypeRepository.save(mdt);
        }
    }

    public List<DietType> getMealDietTypes(int mealId) {
        Meal meal = validateMealExistance(mealId);
        List<MealDietType> mappings = mealDietTypeRepository.findByMealId(mealId);
        List<DietType> dietTypes = mappings.stream()
                .map(mdt -> dietTypeRepository.findById(mdt.getDietTypeId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();
        return dietTypes;
    }
}
