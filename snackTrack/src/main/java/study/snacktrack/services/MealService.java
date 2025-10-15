package study.snacktrack.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import study.snacktrack.entities.EssentialFood;
import study.snacktrack.entities.Ingredient;
import study.snacktrack.entities.Meal;
import study.snacktrack.repositories.FoodRepository;
import study.snacktrack.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import study.snacktrack.dto.EssentialFoodResponse;
import study.snacktrack.dto.IngredientRequest;
import study.snacktrack.dto.IngredientResponse;
import study.snacktrack.dto.MealRequest;
import study.snacktrack.dto.MealResponse;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.IngredientRepository;
import study.snacktrack.repositories.MealRepository;

import jakarta.transaction.Transactional;

@Service
public class MealService {

    @Value("${image.upload-dir:src/main/resources/static/images/meals}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final IngredientRepository ingredientRepository;
    private final FoodRepository foodRepository;
    private final FoodService foodService;

    public MealService(UserRepository userRepository, FoodRepository foodRepository, MealRepository mealRepository, IngredientRepository ingredientRepository, FoodService foodService) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.ingredientRepository = ingredientRepository;
        this.foodRepository = foodRepository;
        this.foodService = foodService;
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

    //o to jest giga kociol - naucz sie :)
    @Transactional
    public boolean createMeal(MealRequest request, int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with this ID doesn't exist");
        }

        if (request.getName() == null || "".equals(request.getName())) {
            throw new IllegalArgumentException("Meal's name must not be empty");

        }

        //dodac mechanizm sprawdzania czy meal o podanej nazwie stnieje juz w bazie - wymagana unikalna nazwa
        if (request.getDescription() == null || "".equals(request.getDescription())) {
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
                essentialFood = foodRepository.findById(ir.getEssentialFoodId()).orElseThrow(() -> new IllegalArgumentException("Food not found"));
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
        System.out.println("Meal added successfully");
        return true;
    }

    @Transactional
    public String uploadMealImage(int mealId, MultipartFile imageFile) throws IOException {
        Meal meal = validateMealExistance(mealId);

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String relativePath = "/images/meals/" + fileName;
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

        return response;
    }

    //edit meal
    public boolean editMealByUser(int mealId, MealRequest request, int userId) {
        Meal meal = validateMealExistance(mealId);
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with this ID doesn't exist");
        }

        if (userId != request.getAuthorId()) {
            throw new IllegalArgumentException("You are not allowed to edit this meal");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Meal's name must not be empty");
        }
        if (request.getDescription() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Meal's description must not be empty");
        }
        if (request.getIngredients() == null || request.getIngredients().isEmpty()) {
            throw new IllegalArgumentException("The meal has to have at least 1 ingredient");
        }

        meal.setName(request.getName());
        meal.setDescription(request.getDescription());

        ingredientRepository.deleteAll(meal.getIngredients());
        //nie mozna set na null bo hibernate wywali blad
        meal.getIngredients().clear();

        for (IngredientRequest ir : request.getIngredients()) {
            EssentialFood essentialFood = null;
            Integer essentialApiId = null;

            Float amout = null;
            Integer pieces = null;

            if (ir.getEssentialFoodId() != null) {
                essentialFood = foodRepository.findById(ir.getEssentialFoodId()).orElseThrow(() -> new IllegalArgumentException("Food not found"));
            } else if (ir.getEssentialApiId() != null) {
                essentialApiId = ir.getEssentialApiId();
            } else {
                throw new IllegalArgumentException("Food must have either essential id or API id");
            }

            if (ir.getAmount() != null) {
                amout = ir.getAmount();
            } else if (ir.getPieces() != null) {
                pieces = ir.getPieces();
            } else {
                throw new IllegalArgumentException("Food must have either pieces or amount");
            }

            Ingredient ingredient = new Ingredient(meal, essentialFood, essentialApiId, amout, pieces);
            meal.addIngredient(ingredient);
        }

        mealRepository.save(meal);
        System.out.println("Meal updated successfully");
        return true;
    }

    //delete meal
    public boolean deleteMealByUser(int mealId, int userId) {
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
        System.out.println("Meal successfully deleted");
        return true;
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
        return mealRepository.findById(mealId).orElseThrow(() -> new IllegalArgumentException("This meal doesn't exist"));
    }
}
