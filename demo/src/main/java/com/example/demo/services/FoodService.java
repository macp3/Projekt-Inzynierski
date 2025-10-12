package com.example.demo.services;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.ApiFoodResponse;
import com.example.demo.entities.EssentialFood;
import com.example.demo.entities.User;
import com.example.demo.repositories.FoodRepository;

@Service
public class FoodService {

    private final RestTemplate restTemplate;
    private final String apiFoodDatabaseKey;
    private final String baseUrl = "https://platform.fatsecret.com/rest";

    private final FoodRepository foodRepository;

    public FoodService(@Value("${API_FOOD_DATABASE_KEY}") String apiFoodDatabaseKey, FoodRepository foodRepository) {
        this.restTemplate = new RestTemplate();
        this.apiFoodDatabaseKey = apiFoodDatabaseKey;
        this.foodRepository = foodRepository;
    }

    public List<ApiFoodResponse> getFoodFromApi(String query) {
        String url = String.format("%s/foods/search?query=%s&api_key=%s", baseUrl, query, apiFoodDatabaseKey);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("foods")) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> foods = (List<Map<String, Object>>) response.get("foods");
        if (foods == null) {
            return Collections.emptyList();
        }

        List<ApiFoodResponse> foodList = foods.stream()
                .limit(20)
                .map(food -> {
                    ApiFoodResponse dto = new ApiFoodResponse();
                    dto.setName((String) food.get("description"));
                    dto.setDefault_weight(((Double) Optional.ofNullable(food.get("servingSize")).orElse(1.0)).intValue());
                    dto.setServingSizeUnit((String) Optional.ofNullable(food.get("servingSizeUnit")).orElse("piece"));
                    dto.setBrandName((String) food.get("brandName"));

                    List<Map<String, Object>> nutrients = (List<Map<String, Object>>) food.get("foodNutrients");
                    if (nutrients != null) {
                        for (Map<String, Object> n : nutrients) {
                            String nutrientName = (String) n.get("nutrientName");
                            double value = ((Number) Optional.ofNullable(n.get("value")).orElse(0)).doubleValue();
                            switch (nutrientName) {
                                case "Energy" ->
                                    dto.setCalorie((int) value);
                                case "Protein" ->
                                    dto.setProtein((float) value);
                                case "Carbohydrate, by difference" ->
                                    dto.setCarbohydrates((float) value);
                                case "Total Sugars" ->
                                    dto.setSugar((float) value);
                                case "Total lipid (fat)" ->
                                    dto.setFat((float) value);
                            }
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        //return sortByNullCount(foodList);
        return foodList;

    }

    public ApiFoodResponse getFoodFromApiById(int fdcId) {
        String url = String.format("%s/food/%d?api_key=%s", baseUrl, fdcId, apiFoodDatabaseKey);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null) {
            throw new RuntimeException("Brak danych dla FDC ID: " + fdcId);
        }

        ApiFoodResponse dto = new ApiFoodResponse();
        dto.setName((String) response.get("description"));
        dto.setBrandName((String) response.getOrDefault("brandName", null));
        dto.setServingSizeUnit((String) response.getOrDefault("servingSizeUnit", "piece"));

        Object servingSizeObj = response.get("servingSize");
        if (servingSizeObj instanceof Number servingSizeNum) {
            dto.setDefault_weight(servingSizeNum.intValue());
        } else {
            dto.setDefault_weight(1);
        }

        Map<String, Object> labelNutrients = (Map<String, Object>) response.get("labelNutrients");
        if (labelNutrients != null) {
            dto.setCalorie((int) extractLabelValue(labelNutrients, "calories"));
            dto.setProtein((float) extractLabelValue(labelNutrients, "protein"));
            dto.setCarbohydrates((float) extractLabelValue(labelNutrients, "carbohydrates"));
            dto.setSugar((float) extractLabelValue(labelNutrients, "sugars"));
            dto.setFat((float) extractLabelValue(labelNutrients, "fat"));
        } else {
            List<Map<String, Object>> nutrients = (List<Map<String, Object>>) response.get("foodNutrients");
            if (nutrients != null) {
                for (Map<String, Object> n : nutrients) {
                    Map<String, Object> nutrient = (Map<String, Object>) n.get("nutrient");
                    if (nutrient == null) {
                        continue;
                    }

                    String name = (String) nutrient.get("name");
                    double value = ((Number) Optional.ofNullable(n.get("amount")).orElse(0)).doubleValue();

                    switch (name) {
                        case "Energy" ->
                            dto.setCalorie((int) value);
                        case "Protein" ->
                            dto.setProtein((float) value);
                        case "Carbohydrate, by difference" ->
                            dto.setCarbohydrates((float) value);
                        case "Total Sugars" ->
                            dto.setSugar((float) value);
                        case "Total lipid (fat)" ->
                            dto.setFat((float) value);
                    }
                }
            }
        }

        return dto;
    }

    private double extractLabelValue(Map<String, Object> labelNutrients, String key) {
        Map<String, Object> nutrient = (Map<String, Object>) labelNutrients.get(key);
        if (nutrient != null && nutrient.get("value") instanceof Number num) {
            return num.doubleValue();
        }
        return 0;
    }

    public EssentialFood addEssentialFood(EssentialFood food, User currentUser) {

        food.setAuthorId(currentUser.getId());

        if (foodRepository.existsByNameIgnoreCase(food.getName())) {
            throw new IllegalArgumentException("Product already exists in the database: " + food.getName());
        }

        if (food.getName().isBlank()) {
            throw new IllegalArgumentException("Field 'name' is required");
        }
        if (food.getDescription().isBlank()) {
            throw new IllegalArgumentException("Field 'description' is required");
        }
        if (food.getCalories() <= 0) {
            throw new IllegalArgumentException("Field 'calories' must be greater than 0");
        }
        if (food.getProtein() < 0) {
            throw new IllegalArgumentException("Field 'protein' must be greater or equal 0");
        }
        if (food.getFat() < 0) {
            throw new IllegalArgumentException("Field 'fat' must be greater or equal 0");
        }
        if (food.getCarbohydrates() < 0) {
            throw new IllegalArgumentException("Field 'carbohydrates' must be greater or equal 0");
        }

        return foodRepository.save(food);
    }

    public List<EssentialFood> searchEssentialFood(String query) {
        return foodRepository.findByNameContainingIgnoreCase(query);
    }
}
