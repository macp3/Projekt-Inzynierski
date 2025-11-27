package study.snacktrack.services;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import study.snacktrack.dto.ApiFoodResponse;
import study.snacktrack.dto.ApiFoodResponseDetailed;
import study.snacktrack.dto.EssentialFoodRequest;
import study.snacktrack.dto.EssentialFoodResponse;
import study.snacktrack.entities.EssentialFood;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.FoodRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service responsible for managing food data.
 * Provides access to FatSecret API and local food repository.
 */
@Service
public class FoodService {

    private final RestTemplate restTemplate;
    private final String baseUrl = "https://platform.fatsecret.com/rest";
    private final FatSecretAuthService authService;

    private final FoodRepository foodRepository;

    /**
     * Constructs FoodService with repository and authentication service.
     *
     * @param foodRepository repository for essential foods
     * @param authService    service for FatSecret authentication
     */
    public FoodService(FoodRepository foodRepository, FatSecretAuthService authService) {
        this.restTemplate = new RestTemplate();
        this.foodRepository = foodRepository;
        this.authService = authService;
    }

    /**
     * Safely parses integer values.
     *
     * @param value        input object
     * @param defaultValue fallback value
     * @return parsed integer or default
     */
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

    /**
     * Safely parses float values.
     *
     * @param value        input object
     * @param defaultValue fallback value
     * @return parsed float or default
     */
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

    /**
     * Retrieves all essential foods from repository.
     *
     * @return list of essential food responses
     */
    public List<EssentialFoodResponse> getAllEssentials() {
        List<EssentialFood> essentials = foodRepository.findAll();
        List<EssentialFoodResponse> response = new ArrayList<>();
        for (EssentialFood e : essentials) {
            EssentialFoodResponse r = new EssentialFoodResponse(e);
            response.add(r);
        }
        return response;
    }

    /**
     * Searches food items in FatSecret API.
     *
     * @param query search expression
     * @return list of food responses
     */
    public List<ApiFoodResponse> getFoodFromApi(String query) {
        String apiFoodDatabaseKey = authService.getAccessToken();
        String url = String.format(
                "%s/rest/foods/search/v1?method=foods.search&search_expression=%s&format=json",
                baseUrl,
                URLEncoder.encode(query, StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiFoodDatabaseKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> response = responseEntity.getBody();

        if (response != null && response.containsKey("error")) {
            System.err.println("FATSECRET API ERROR: " + response.get("error"));
        }

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

    /**
     * Retrieves detailed food information from FatSecret API by ID.
     *
     * @param foodId identifier of the food
     * @return detailed food response
     */
    public ApiFoodResponseDetailed getFoodFromApiById(long foodId) {
        String apiFoodDatabaseKey = authService.getAccessToken();
        String url = String.format(
                "%s/rest/food/v5?method=food.get.v5&food_id=%d&format=json",
                baseUrl, foodId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiFoodDatabaseKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> response = responseEntity.getBody();

        if (response == null || !response.containsKey("food")) {
            throw new RuntimeException("No data for food_id: " + foodId);
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

    /**
     * Extracts nutrient value from description string.
     *
     * @param description  food description
     * @param nutrientName nutrient name
     * @return nutrient value or 0
     */
    private double parseNutrient(String description, String nutrientName) {
        Pattern pattern = Pattern.compile(nutrientName + ":\\s*([0-9.]+)");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0;
    }

    /**
     * Extracts quantity information from description string.
     *
     * @param description food description
     * @return quantity string or null
     */
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

    /**
     * Adds a new essential food to repository after validation.
     *
     * @param request     food request data
     * @param currentUser user adding the food
     * @return confirmation message
     */
    @Transactional
    public String addEssentialFood(EssentialFoodRequest request, User currentUser) {

        if (foodRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Product already exists in the database: " + request.getName());
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Field 'name' is required");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Field 'description' is required");
        }
        if (request.getCalories() <= 0) {
            throw new IllegalArgumentException("Field 'calories' must be greater than 0");
        }
        if (request.getProtein() < 0) {
            throw new IllegalArgumentException("Field 'protein' must be greater or equal 0");
        }
        if (request.getFat() < 0) {
            throw new IllegalArgumentException("Field 'fat' must be greater or equal 0");
        }
        if (request.getCarbohydrates() < 0) {
            throw new IllegalArgumentException("Field 'carbohydrates' must be greater or equal 0");
        }
        if (request.getDefaultWeight() < 0) {
            throw new IllegalArgumentException("Field 'defaultWeight' must be greater or equal 0");
        }
        if (request.getServingSizeUnit() == null || request.getServingSizeUnit().isBlank()) {
            throw new IllegalArgumentException("Field 'servingSizeUnit' is required");
        }

        EssentialFood food = new EssentialFood();

        food.setId(food.getId());
        food.setAuthorId(currentUser.getId());
        food.setName(request.getName());
        food.setCalories(request.getCalories());
        food.setCarbohydrates(request.getCarbohydrates());
        food.setDescription(request.getDescription());
        food.setFat(request.getFat());
        food.setProtein(request.getProtein());
        food.setBrandName(request.getBrandName());
        food.setDefaultWeight(request.getDefaultWeight());
        food.setServingSizeUnit(request.getServingSizeUnit());

        foodRepository.save(food);
        return "Food has been added to database";
    }

    /**
     * Searches essential foods in repository by name.
     *
     * @param query search string
     * @return list of matching essential foods
     */
    public List<EssentialFood> searchEssentialFood(String query) {
        return foodRepository.findByNameContainingIgnoreCase(query);
    }
}
