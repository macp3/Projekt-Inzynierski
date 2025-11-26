package study.snacktrack.services;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import study.snacktrack.dto.ApiFoodResponseDetailed;
import study.snacktrack.dto.MealResponse;
import study.snacktrack.dto.RegisteredAlimentationRequest;
import study.snacktrack.dto.RegisteredAlimentationResponse;
import study.snacktrack.entities.*;
import study.snacktrack.entities.enums.MealNames;
import study.snacktrack.repositories.*;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
    private final MealService mealService;

    private final Cache<Integer, ApiFoodResponseDetailed> apiFoodCache = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    public RegisteredAlimentationService(
            UserRepository userRepository,
            FoodRepository foodRepository,
            MealRepository mealRepository,
            RegisteredAlimentationRepository repository,
            JwtService jwtService,
            FoodService foodService,
            MealService mealService) {
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.mealRepository = mealRepository;
        this.repository = repository;
        this.jwtService = jwtService;
        this.foodService = foodService;
        this.mealService = mealService;
    }

    private ApiFoodResponseDetailed getApiFoodWithCache(Integer apiId) {
        if (apiId == null)
            return null;
        try {
            return apiFoodCache.get(apiId, id -> {
                try {
                    return foodService.getFoodFromApiById(id);
                } catch (Exception e) {
                    return null;
                }
            });
        } catch (Exception e) {
            return null;
        }
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
            if (getApiFoodWithCache(dto.getMealApiId()) == null) {
                System.out.println("Warning: Adding entry with API ID " + dto.getMealApiId()
                        + " but API failed to resolve details.");
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
                    if (entry.getMealName() != null) {
                        dto.setMealName(entry.getMealName());
                    } else {
                        dto.setMealName(MealNames.snack);
                    }
                    if (entry.getMeal() != null) {
                        try {
                            MealResponse fullMeal = mealService.getMealWithIngredients(entry.getMeal().getId());
                            dto.setMeal(fullMeal);
                        } catch (Exception e) {
                            System.err.println("Error fetching full meal details: " + e.getMessage());
                        }
                    }
                    if (entry.getEssentialFood() == null && entry.getMealApiId() != null) {
                        dto.setMealApi(getApiFoodWithCache(entry.getMealApiId()));
                    }
                    return dto;
                })
                .toList();
    }

    public void deleteEntry(String authHeader, Integer id) {
        User user = getUserFromToken(authHeader);
        RegisteredAlimentation entry = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));
        if (!entry.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this entry");
        }
        repository.delete(entry);
    }

    public RegisteredAlimentation updateEntry(String authHeader, Integer id, RegisteredAlimentationRequest dto) {
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
            if (dto.getAmount() <= 0)
                throw new IllegalArgumentException("Amount can't be smaller or equal 0");
            entry.setAmount(dto.getAmount());
            entry.setPieces(null);
        }
        if (dto.getPieces() != null) {
            if (dto.getPieces() <= 0)
                throw new IllegalArgumentException("Pieces can't be smaller or equal 0");
            entry.setPieces(dto.getPieces());
            entry.setAmount(null);
        }
        return repository.save(entry);
    }

    @Transactional
    public String copyMeal(String authHeader, String fromDate, MealNames fromMealName, String toDate,
            MealNames toMealName) {
        User user = getUserFromToken(authHeader);
        LocalDate from = parseDate(fromDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid source date"));
        LocalDate to = parseDate(toDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid target date"));

        System.out.println("COPYING: User=" + user.getId() + ", From=" + from + " (" + fromMealName + ") To=" + to
                + " (" + toMealName + ")");

        // 1. Używamy precyzyjnego zapytania z repozytorium
        List<RegisteredAlimentation> sourceEntries = repository.findByUserIdAndTimestampAndMealName(user.getId(), from,
                fromMealName);

        if (sourceEntries.isEmpty()) {
            // To nie jest błąd krytyczny, po prostu nic nie kopiujemy
            return "No entries to copy from " + fromMealName;
        }

        List<RegisteredAlimentation> newEntries = new ArrayList<>();

        // 2. Tworzymy czyste kopie
        for (RegisteredAlimentation entry : sourceEntries) {
            RegisteredAlimentation copy = new RegisteredAlimentation();
            copy.setUserId(user.getId());
            // Kopiujemy referencje
            copy.setEssentialFood(entry.getEssentialFood());
            copy.setMeal(entry.getMeal());
            copy.setMealApiId(entry.getMealApiId());
            // Kopiujemy wartości
            copy.setAmount(entry.getAmount());
            copy.setPieces(entry.getPieces());
            // Ustawiamy nową datę i nazwę posiłku
            copy.setTimestamp(to);
            copy.setMealName(toMealName);

            newEntries.add(copy);
        }

        // 3. Zapisujemy wszystko naraz
        repository.saveAll(newEntries);

        return "Copied " + newEntries.size() + " items successfully";
    }
}