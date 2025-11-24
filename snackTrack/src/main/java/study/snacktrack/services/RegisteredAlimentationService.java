package study.snacktrack.services;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import study.snacktrack.dto.MealResponse; // Upewnij się, że masz ten import
import study.snacktrack.dto.RegisteredAlimentationRequest;
import study.snacktrack.dto.RegisteredAlimentationResponse;
import study.snacktrack.entities.*;
import study.snacktrack.entities.enums.MealNames;
import study.snacktrack.repositories.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class RegisteredAlimentationService {

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final MealRepository mealRepository;
    private final RegisteredAlimentationRepository repository;
    private final JwtService jwtService;
    private final FoodService foodService;
    // 1. DODAJEMY MEAL SERVICE
    private final MealService mealService;

    public RegisteredAlimentationService(
            UserRepository userRepository,
            FoodRepository foodRepository,
            MealRepository mealRepository,
            RegisteredAlimentationRepository repository,
            JwtService jwtService,
            FoodService foodService,
            MealService mealService) { // 2. DODAJEMY DO KONSTRUKTORA
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.mealRepository = mealRepository;
        this.repository = repository;
        this.jwtService = jwtService;
        this.foodService = foodService;
        this.mealService = mealService;
    }

    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Optional<LocalDate> parseDate(String date) {
        if (date == null || date.isEmpty())
            return Optional.empty();
        try {
            return Optional.of(LocalDate.parse(date));
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use YYYY-MM-DD");
        }
    }

    public String addEntry(String authHeader, RegisteredAlimentationRequest dto, String date) {
        // ... (Ta metoda pozostaje bez zmian) ...
        User user = getUserFromToken(authHeader);

        RegisteredAlimentation entry = new RegisteredAlimentation();
        entry.setUserId(user.getId());

        if (dto.getEssentialId() != null) {
            EssentialFood essential = foodRepository.findById(dto.getEssentialId())
                    .orElseThrow(() -> new IllegalArgumentException("Wrong essential ID"));
            entry.setEssentialFood(essential);
        } else if (dto.getMealId() != null) {
            Meal meal = mealRepository.findById(dto.getMealId())
                    .orElseThrow(() -> new IllegalArgumentException("Wrong meal ID"));
            entry.setMeal(meal);
        } else if (dto.getMealApiId() != null) {
            try {
                foodService.getFoodFromApiById(dto.getMealApiId());
            } catch (Exception e) {
                throw new IllegalArgumentException("Wrong api ID");
            }
            entry.setMealApiId(dto.getMealApiId());
        }

        if (dto.getAmount() == null && dto.getPieces() == null) {
            throw new IllegalArgumentException("You have to specify amount or pieces");
        }

        if (dto.getAmount() != null && dto.getMealId() == null) {
            entry.setAmount(dto.getAmount());
        } else if (dto.getPieces() != null) {
            entry.setPieces(dto.getPieces());
        }

        LocalDate entryDate;
        if (date != null && !date.isEmpty()) {
            try {
                entryDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD");
            }
        } else if (dto.getTimestamp() != null) {
            entryDate = dto.getTimestamp();
        } else {
            entryDate = LocalDate.now();
        }
        entry.setTimestamp(entryDate);

        boolean exists = Arrays.asList(MealNames.values()).contains(dto.getMealName());
        if (dto.getMealName() == null || !exists) {
            throw new IllegalArgumentException("There is no such meal");
        }

        entry.setMealName(dto.getMealName());

        repository.save(entry);
        return "Meal registered";
    }

    public List<RegisteredAlimentationResponse> getMyEntries(String authHeader, String date) {
        User user = getUserFromToken(authHeader);

        List<RegisteredAlimentation> myEntries = parseDate(date)
                .map(parsedDate -> repository.findByUserIdAndTimestamp(user.getId(), parsedDate))
                .orElseGet(() -> repository.findByUserId(user.getId()));

        return myEntries.stream()
                .map(entry -> {
                    RegisteredAlimentationResponse dto = new RegisteredAlimentationResponse(entry);

                    // mealName przypisane bezpośrednio (enum)
                    if (entry.getMealName() != null) {
                        dto.setMealName(entry.getMealName());
                    } else {
                        dto.setMealName(MealNames.snack); // fallback
                    }

                    // 3. NOWA LOGIKA DLA PRZEPISÓW (MEALS)
                    if (entry.getMeal() != null) {
                        try {
                            // Używamy MealService, który ma logikę pobierania danych z API dla składników
                            MealResponse fullMeal = mealService.getMealWithIngredients(entry.getMeal().getId());
                            // Nadpisujemy 'pusty' obiekt Meal w DTO tym pełnym, zawierającym dane z API
                            dto.setMeal(fullMeal);
                        } catch (Exception e) {
                            System.err.println("Error fetching full meal details: " + e.getMessage());
                        }
                    }

                    // Pobranie danych z Meal API jeśli essentialFood jest null (Dla pojedynczych
                    // produktów z API)
                    if (entry.getEssentialFood() == null && entry.getMealApiId() != null) {
                        try {
                            dto.setMealApi(foodService.getFoodFromApiById(entry.getMealApiId()));
                        } catch (Exception e) {
                            System.err.println("Error fetching from API: " + e.getMessage());
                        }
                    }

                    return dto;
                })
                .toList();
    }

    public void deleteEntry(String authHeader, Integer id) {
        // ... (Bez zmian)
        User user = getUserFromToken(authHeader);

        RegisteredAlimentation entry = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));

        if (!entry.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this entry");
        }

        repository.delete(entry);
    }

    public RegisteredAlimentation updateEntry(String authHeader, Integer id, RegisteredAlimentationRequest dto) {
        // ... (Bez zmian)
        User user = getUserFromToken(authHeader);

        RegisteredAlimentation entry = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));

        if (!entry.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this entry");
        }

        if (dto.getAmount() == null && dto.getPieces() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have to specify amount or pieces");
        }

        if (dto.getAmount() != null) {
            if (dto.getAmount() <= 0) {
                throw new IllegalArgumentException("Amount can't be smaller or equal 0");
            }
            entry.setAmount(dto.getAmount());
            entry.setPieces(null);
        }

        if (dto.getPieces() != null) {
            if (dto.getPieces() <= 0) {
                throw new IllegalArgumentException("Pieces can't be smaller or equal 0");
            }
            entry.setPieces(dto.getPieces());
            entry.setAmount(null);
        }

        return repository.save(entry);
    }

    @Transactional
    public String copyMeal(String authHeader,
            String fromDate,
            MealNames fromMealName,
            String toDate,
            MealNames toMealName) {
        // ... (Bez zmian)
        User user = getUserFromToken(authHeader);

        LocalDate from = parseDate(fromDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid source date"));
        LocalDate to = parseDate(toDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid target date"));

        // pobierz wszystkie wpisy dla posiłku źródłowego
        List<RegisteredAlimentation> sourceEntries = repository.findByUserIdAndTimestampAndMealName(user.getId(), from,
                fromMealName);

        if (sourceEntries.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No entries to copy");
        }

        // skopiuj każdy wpis do nowego posiłku
        for (RegisteredAlimentation entry : sourceEntries) {
            RegisteredAlimentation copy = new RegisteredAlimentation();
            copy.setUserId(user.getId());
            copy.setEssentialFood(entry.getEssentialFood());
            copy.setMeal(entry.getMeal());
            copy.setMealApiId(entry.getMealApiId());
            copy.setAmount(entry.getAmount());
            copy.setPieces(entry.getPieces());
            copy.setTimestamp(to);
            copy.setMealName(toMealName);

            repository.save(copy);
        }

        return "Meal copied successfully from " + fromMealName + " " + fromDate +
                " to " + toMealName + " " + toDate;
    }

}