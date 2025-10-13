package com.example.demo.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.RegisteredAlimentationRequest;
import com.example.demo.dto.RegisteredAlimentationResponse;
import com.example.demo.entities.EssentialFood;
import com.example.demo.entities.Meal;
import com.example.demo.entities.RegisteredAlimentation;
import com.example.demo.entities.User;
import com.example.demo.repositories.FoodRepository;
import com.example.demo.repositories.MealRepository;
import com.example.demo.repositories.RegisteredAlimentationRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.FoodService;
import com.example.demo.services.JwtService;
import com.example.demo.services.MealService;

@RestController
@RequestMapping("/registered")
public class RegisteredAlimentationController {

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final MealRepository mealRepository;
    private final RegisteredAlimentationRepository repository;
    private final JwtService jwtService;
    private final FoodService foodService;
    private final MealService mealService;

    public RegisteredAlimentationController(FoodRepository foodRepository, JwtService jwtService, MealRepository mealRepository, RegisteredAlimentationRepository repository, UserRepository userRepository, FoodService foodService, MealService mealService) {
        this.foodRepository = foodRepository;
        this.jwtService = jwtService;
        this.mealRepository = mealRepository;
        this.repository = repository;
        this.userRepository = userRepository;
        this.foodService = foodService;
        this.mealService = mealService;
    }

    @PostMapping("/add")
    public ResponseEntity<RegisteredAlimentation> addEntry(@RequestBody RegisteredAlimentationRequest dto, @RequestHeader("Authorization") String authHeader, @RequestParam(required = false) String date) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        RegisteredAlimentation entry = new RegisteredAlimentation();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        entry.setUserId(user.getId());

        if (dto.getEssentialId() != null) {
            EssentialFood essential = foodRepository.findById(dto.getEssentialId())
                    .orElse(null);
            entry.setEssentialFood(essential);
        } else if (dto.getMealId() != null) {
            Meal meal = mealRepository.findById(dto.getMealId())
                    .orElse(null);
            entry.setMeal(meal);
        } else if (dto.getMealApiId() != null) {
            entry.setMealApiId(dto.getMealApiId());
        }

        if (dto.getAmount() != null) {
            entry.setAmount(dto.getAmount());
        } else if (dto.getPieces() != null) {
            entry.setPieces(dto.getPieces());
        }

        LocalDate entryDate;
        if (date != null && !date.isEmpty()) {
            try {
                entryDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use YYYY-MM-DD");
            }
        } else if (dto.getTimestamp() != null) {
            entryDate = dto.getTimestamp();
        } else {
            entryDate = LocalDate.now();
        }

        entry.setTimestamp(entryDate);

        RegisteredAlimentation savedEntry = repository.save(entry);
        return ResponseEntity.ok(savedEntry);
    }

    @GetMapping("/my")
    public ResponseEntity<List<RegisteredAlimentationResponse>> getMyEntries(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String date
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<RegisteredAlimentation> myEntries;

        if (date != null && !date.isEmpty()) {
            try {
                LocalDate parsedDate = LocalDate.parse(date);
                myEntries = repository.findByUserIdAndTimestamp(user.getId(), parsedDate);
            } catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use YYYY-MM-DD");
            }
        } else {
            myEntries = repository.findByUserId(user.getId());
        }

        List<RegisteredAlimentationResponse> responses;
        responses = myEntries.stream()
                .map(entry -> {
                    RegisteredAlimentationResponse dto = new RegisteredAlimentationResponse(entry);

                    if (entry.getEssentialFood() == null && entry.getMealApiId() != null) {
                        dto.setMealApi(foodService.getFoodFromApiById(entry.getMealApiId()));
                    }

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(responses);
    }

}
