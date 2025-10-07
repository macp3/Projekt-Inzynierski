package com.example.demo.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiFoodResponse;
import com.example.demo.services.ExternalApiService;

@RestController
@RequestMapping("/food")
public class FoodController {

    private final ExternalApiService ExternalApiService;

    public FoodController(ExternalApiService ExternalApiService) {
        this.ExternalApiService = ExternalApiService;
    }

    @GetMapping("/api")
    public List<ApiFoodResponse> getFoodFromApi(@RequestParam String query) {
        return ExternalApiService.getFoodFromApi(query);
    }
}
