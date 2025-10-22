package study.snacktrack.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import study.snacktrack.dto.RegisteredAlimentationRequest;
import study.snacktrack.dto.RegisteredAlimentationResponse;
import study.snacktrack.entities.*;
import study.snacktrack.repositories.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class RegisteredAlimentationService {

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final MealRepository mealRepository;
    private final RegisteredAlimentationRepository repository;
    private final JwtService jwtService;
    private final FoodService foodService;

    public RegisteredAlimentationService(
            UserRepository userRepository,
            FoodRepository foodRepository,
            MealRepository mealRepository,
            RegisteredAlimentationRepository repository,
            JwtService jwtService,
            FoodService foodService) {
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.mealRepository = mealRepository;
        this.repository = repository;
        this.jwtService = jwtService;
        this.foodService = foodService;
    }

    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
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

        repository.save(entry);
        return "Meal registered";
    }

    public List<RegisteredAlimentationResponse> getMyEntries(String authHeader, String date) {
        User user = getUserFromToken(authHeader);
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

        return myEntries.stream()
                .map(entry -> {
                    RegisteredAlimentationResponse dto = new RegisteredAlimentationResponse(entry);
                    if (entry.getEssentialFood() == null && entry.getMealApiId() != null) {
                        try {
                            dto.setMealApi(foodService.getFoodFromApiById(entry.getMealApiId()));
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
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
}
