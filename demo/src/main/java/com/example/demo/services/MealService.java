package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.dto.EssentialFoodResponse;
import com.example.demo.dto.IngredientRequest;
import com.example.demo.dto.IngredientResponse;
import com.example.demo.dto.MealRequest;
import com.example.demo.dto.MealResponse;
import com.example.demo.entities.EssentialFood;
import com.example.demo.entities.Ingredient;
import com.example.demo.entities.Meal;
import com.example.demo.repositories.FoodRepository;
import com.example.demo.repositories.IngredientRepository;
import com.example.demo.repositories.MealRepository;
import com.example.demo.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class MealService {

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final IngredientRepository ingredientRepository;
    private final FoodRepository foodRepository;

    public MealService(UserRepository userRepository, FoodRepository foodRepository, MealRepository mealRepository, IngredientRepository ingredientRepository, FoodRepository foodRepository1) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.ingredientRepository = ingredientRepository;
        this.foodRepository = foodRepository1;
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
    public Meal createMeal(MealRequest request, int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with this ID doesn't exist");
        }

        if (request.getName() == null || "".equals(request.getName())) {
            throw new IllegalArgumentException("Meal's name must not be empty");

        }

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

            System.out.println(ir.getEssentialFoodId() + " " + ir.getEssentialApiId());

            if (ir.getEssentialFoodId() != null) {
                essentialFood = foodRepository.findById(ir.getEssentialFoodId()).orElseThrow(() -> new IllegalArgumentException("Food not found"));
            } else if (ir.getEssentialApiId() != null) {
                essentialApiId = ir.getEssentialApiId();
            } else {
                throw new IllegalArgumentException("Food must have either essential id or API id");
            }

            Ingredient ingredient = new Ingredient(meal, essentialFood, essentialApiId, ir.getAmount(), ir.getDefaultUnit());
            meal.addIngredient(ingredient);
        }

        mealRepository.save(meal);
        System.out.println("Meal added successfully");
        return meal;
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
                    dto.setDefaultUnit(ingredient.getDefaultUnit());

                    if (ingredient.getEssentialApiId() != null) {
                        dto.setEssentialApiId(ingredient.getEssentialApiId());
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

    public List<Meal> findAllMeals() {
        return mealRepository.findAll();
    }

    public List<Meal> getMealsByUser(int userId) {
        return mealRepository.findByAuthorId(userId);
    }

    public List<Meal> searchMealsByName(String name) {
        return mealRepository.findByNameContainingIgnoreCase(name);
    }
}
