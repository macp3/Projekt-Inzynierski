package com.example.demo.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.ApiFoodResponse;
import com.example.demo.dto.ApiFoodResponseDetailed;
import com.example.demo.entities.EssentialFood;
import com.example.demo.entities.User;
import com.example.demo.repositories.FoodRepository;

@Service
public class FoodService {

    private final RestTemplate restTemplate;
    private final String baseUrl = "https://platform.fatsecret.com/rest";
    private final FatSecretAuthService authService;

    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository, FatSecretAuthService authService) {
        this.restTemplate = new RestTemplate();
        this.foodRepository = foodRepository;
        this.authService = authService;
    }

    private int parseIntSafe(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Number num) {
                return num.intValue();
            }
            return (int) Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private float parseFloatSafe(Object value, float defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Number num) {
                return num.floatValue();
            }
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public List<ApiFoodResponse> getFoodFromApi(String query) {
        String apiFoodDatabaseKey = authService.getAccessToken();
        String url = String.format(
                "%s/rest/foods/search/v1?method=foods.search&search_expression=%s&format=json",
                baseUrl,
                URLEncoder.encode(query, StandardCharsets.UTF_8)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiFoodDatabaseKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> response = responseEntity.getBody();

        if (response == null || !response.containsKey("foods")) {
            return Collections.emptyList();
        }

        Map<String, Object> foodsObj = (Map<String, Object>) response.get("foods");
        if (foodsObj == null || !foodsObj.containsKey("food")) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> foods = (List<Map<String, Object>>) foodsObj.get("food");

        return foods.stream().limit(20).map(food -> {
            ApiFoodResponse dto = new ApiFoodResponse();
            dto.setId(parseIntSafe(food.get("food_id"), 0));
            dto.setName((String) food.get("food_name"));
            dto.setBrandName((String) food.getOrDefault("brand_name", null));

            String desc = (String) food.get("food_description");
            if (desc != null) {
                dto.setQuantity(parseQuantity(desc));
                dto.setCalorie((int) parseNutrient(desc, "Calories"));
                dto.setFat(parseFloatSafe(parseNutrient(desc, "Fat"), 0));
                dto.setCarbohydrates(parseFloatSafe(parseNutrient(desc, "Carbs"), 0));
                dto.setProtein(parseFloatSafe(parseNutrient(desc, "Protein"), 0));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public ApiFoodResponseDetailed getFoodFromApiById(long foodId) {
        String apiFoodDatabaseKey = authService.getAccessToken();
        String url = String.format(
                "%s/rest/food/v5?method=food.get.v5&food_id=%d&format=json",
                baseUrl, foodId
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiFoodDatabaseKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> response = responseEntity.getBody();

        if (response == null || !response.containsKey("food")) {
            throw new RuntimeException("Brak danych dla food_id: " + foodId);
        }

        Map<String, Object> food = (Map<String, Object>) response.get("food");
        ApiFoodResponseDetailed dto = new ApiFoodResponseDetailed();
        dto.setId(parseIntSafe(food.get("food_id"), 0));
        dto.setName((String) food.get("food_name"));
        dto.setBrandName((String) food.getOrDefault("brand_name", null));

        Map<String, Object> servingsObj = (Map<String, Object>) food.get("servings");
        if (servingsObj != null && servingsObj.containsKey("serving")) {
            List<Map<String, Object>> servings = (List<Map<String, Object>>) servingsObj.get("serving");
            if (!servings.isEmpty()) {
                Map<String, Object> s = servings.get(0);

                dto.setDefaultWeight(parseFloatSafe(s.get("metric_serving_amount"), 1));
                dto.setServingSizeUnit((String) s.getOrDefault("metric_serving_unit", "piece"));
                dto.setCalorie(parseIntSafe(s.get("calories"), 0));
                dto.setProtein(parseFloatSafe(s.get("protein"), 0));
                dto.setCarbohydrates(parseFloatSafe(s.get("carbohydrate"), 0));
                dto.setFat(parseFloatSafe(s.get("fat"), 0));
            }
        }

        return dto;
    }

    private double parseNutrient(String description, String nutrientName) {
        Pattern pattern = Pattern.compile(nutrientName + ":\\s*([0-9.]+)");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0;
    }

    private String parseQuantity(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        int dashIndex = description.indexOf(" -");
        if (dashIndex != -1) {
            return description.substring(0, dashIndex).trim();
        }
        return description.trim();
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
