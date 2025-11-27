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
import java.util.*;

/**
 * Service responsible for managing registered alimentation entries.
 * Provides functionality for adding, retrieving, updating, deleting, and copying meal entries.
 */
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

    /**
     * Constructs RegisteredAlimentationService with required repositories and services.
     */
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

    /**
     * Retrieves food details from API with caching.
     *
     * @param apiId API food identifier
     * @return detailed food response or null
     */
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

    /**
     * Extracts user from JWT token.
     *
     * @param authHeader authorization header
     * @return User entity
     */
    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    /**
     * Parses date string into LocalDate.
     *
     * @param date date string
     * @return optional LocalDate
     */
    private Optional<LocalDate> parseDate(String date) {
        if (date == null || date.isEmpty())
            return Optional.empty();
        try {
            return Optional.of(LocalDate.parse(date));
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use YYYY-MM-DD");
        }
    }

    /**
     * Adds a new alimentation entry.
     *
     * @param authHeader authorization header
     * @param dto alimentation request data
     * @param date optional date string
     * @return confirmation message
     */
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

    /**
     * Retrieves alimentation entries for current user.
     *
     * @param authHeader authorization header
     * @param date optional date string
     * @return list of alimentation responses
     */
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

    /**
     * Deletes alimentation entry by ID.
     *
     * @param authHeader authorization header
     * @param id entry identifier
     */
    public void deleteEntry(String authHeader, Integer id) {
        User user = getUserFromToken(authHeader);
        RegisteredAlimentation entry = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found"));
        if (!entry.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this entry");
        }
        repository.delete(entry);
    }

    /**
     * Updates alimentation entry by ID.
     *
     * @param authHeader authorization header
     * @param id entry identifier
     * @param dto updated request data
     * @return updated entry
     */
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

    /**
     * Copies meal entries from one date and meal name to another.
     *
     * @param authHeader authorization header
     * @param fromDate source date string
     * @param fromMealName source meal name
     * @param toDate target date string
     * @param toMealName target meal name
     * @return confirmation message
     */
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

        // Retrieve source entries
        List<RegisteredAlimentation> sourceEntries = repository.findByUserIdAndTimestampAndMealName(user.getId(), from,
                fromMealName);

        if (sourceEntries.isEmpty()) {
            return "No entries to copy from " + fromMealName;
        }

        List<RegisteredAlimentation> newEntries = new ArrayList<>();

        // Create copies of source entries
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

            newEntries.add(copy);
        }

        // Save all new entries
        repository.saveAll(newEntries);

        return "Copied " + newEntries.size() + " items successfully";
    }
}
