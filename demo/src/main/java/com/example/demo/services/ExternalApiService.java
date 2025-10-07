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

@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;
    private final String apiFoodDatabaseKey;
    private final String baseUrl = "https://api.nal.usda.gov/fdc/v1";

    public ExternalApiService(@Value("${API_FOOD_DATABASE_KEY}") String apiFoodDatabaseKey) {
        this.restTemplate = new RestTemplate();
        this.apiFoodDatabaseKey = apiFoodDatabaseKey;
    }

    public List<ApiFoodResponse> getFoodFromApi(String query) {
        String url = String.format("%s/foods/search?query=%s&api_key=%s", baseUrl, query, apiFoodDatabaseKey);

        // Wywołanie synchroniczne za pomocą RestTemplate
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("foods")) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> foods = (List<Map<String, Object>>) response.get("foods");
        if (foods == null) {
            return Collections.emptyList();
        }

        return foods.stream()
                .limit(10)
                .map(food -> {
                    ApiFoodResponse dto = new ApiFoodResponse();
                    dto.setName((String) food.get("description"));
                    dto.setDefault_weight(((Double) Optional.ofNullable(food.get("servingSize")).orElse(0.0)).intValue());
                    dto.setServingSizeUnit((String) food.get("servingSizeUnit"));

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
    }
}
