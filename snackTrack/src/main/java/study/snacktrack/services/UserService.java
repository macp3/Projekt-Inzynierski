package study.snacktrack.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import study.snacktrack.dto.BodyParametersResponse;
import study.snacktrack.entities.*;
import study.snacktrack.entities.enums.Sex;
import study.snacktrack.entities.enums.Status;
import study.snacktrack.repositories.*;

@Service
public class UserService {

    @Value("${image.upload-dir:src/main/resources/static/images/profiles}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final BodyParametersRepository bodyParametersRepository;
    private final UserDeviceTokenRepository deviceTokenRepository;
    private final FavouriteRepository favouriteRepository;
    private final MealRepository mealRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BodyParametersRepository bodyParametersRepository,
            UserDeviceTokenRepository deviceTokenRepository, FavouriteRepository favouriteRepository,
            MealRepository mealRepository) {
        this.userRepository = userRepository;
        this.bodyParametersRepository = bodyParametersRepository;
        this.deviceTokenRepository = deviceTokenRepository;
        this.favouriteRepository = favouriteRepository;
        this.mealRepository = mealRepository;
    }

    // nie dziala XDDD
    // tera sie nie moge zalogoiwac
    // jednak dziala ale user3 stracony
    public User changePassword(int userId, String password) {
        User user = getUserById(userId);

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        if (passwordEncoder.matches(password, user.getPassword()))
            throw new IllegalArgumentException("New password must be different from the old password");

        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return user;
    }

    // do rozbudowania:
    // po drugie pole estimated_time zeby nie przekraczac deficytu/nadwyzki 1000kcal
    // (bo to niezdrowe)
    // TAK DZIALA FITATU
    @Transactional
    public BodyParametersResponse changeBodyParameters(
            Integer userId,
            Sex sex,
            Float height,
            Float weight,
            Integer age,
            Float dailyActivityFactor,
            Float dailyActivityTrainingFactor,
            Float weeklyWeightChangeTempo,
            Float goalWeight) {
        BodyParameters existing = bodyParametersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

        if (sex != null) {
            if (sex != Sex.male && sex != Sex.female)
                throw new IllegalArgumentException("Sex must be male or female");
            existing.setSex(sex);
        }

        if (height != null) {
            if (height <= 0)
                throw new IllegalArgumentException("Height must be greater than zero");
            existing.setHeight(height);
        }

        if (weight != null) {
            if (weight <= 0)
                throw new IllegalArgumentException("Weight must be greater than zero");
            existing.setWeight(weight);
        }

        if (age != null) {
            if (age <= 0)
                throw new IllegalArgumentException("Age must be greater than zero");
            existing.setAge(age);
        }

        if (dailyActivityFactor != null) {
            if (Float.compare(dailyActivityFactor, 0.7f) < 0 || Float.compare(dailyActivityFactor, 1.15f) > 0)
                throw new IllegalArgumentException("Daily activity factor must be between 0.7 and 1.15");
            existing.setDailyActivityFactor(dailyActivityFactor);
        }

        if (dailyActivityTrainingFactor != null) {
            if (Float.compare(dailyActivityTrainingFactor, 0.5f) < 0
                    || Float.compare(dailyActivityTrainingFactor, 0.95f) > 0)
                throw new IllegalArgumentException("Daily activity training factor must be between 0.5 and 0.95");
            existing.setDailyActivityTrainingFactor(dailyActivityTrainingFactor);
        }

        if (weeklyWeightChangeTempo != null) {
            if (weeklyWeightChangeTempo < 0 || weeklyWeightChangeTempo > 1)
                throw new IllegalArgumentException("Weekly weight change tempo must be between 0 and 1");
            existing.setWeeklyWeightChangeTempo(weeklyWeightChangeTempo);
        }

        if (goalWeight != null) {
            if (goalWeight <= 0)
                throw new IllegalArgumentException("Goal weight must be greater than zero");
            existing.setGoalWeight(goalWeight);
        }

        Sex finalSex = existing.getSex();
        float finalHeight = existing.getHeight();
        float finalWeight = existing.getWeight();
        int finalAge = existing.getAge();
        float finalDailyActivityFactor = existing.getDailyActivityFactor();
        float finalDailyActivityTrainingFactor = existing.getDailyActivityTrainingFactor();
        float finalWeeklyWeightChangeTempo = existing.getWeeklyWeightChangeTempo();
        float finalGoalWeight = existing.getGoalWeight();

        if (finalGoalWeight == finalWeight) {
            finalWeeklyWeightChangeTempo = 0f;
            System.out.println("SETTING WEEKLY WEIGHT CHANGE TEMPO TO 0 SINCE YOU DON'T WANT TO CHANGE YOUR WEIGHT");
            existing.setWeeklyWeightChangeTempo(0f);
        }

        float formula = (finalSex == Sex.male)
                ? (10 * finalWeight) + (6.25f * finalHeight) - (5 * finalAge) + 5
                : (10 * finalWeight) + (6.25f * finalHeight) - (5 * finalAge) - 161;

        float caloricZero = formula * (finalDailyActivityFactor + finalDailyActivityTrainingFactor);
        float calorieLimit = caloricZero;
        float proteinLimit;
        float fatLimit;
        float carbohydratesLimit;

        if (finalGoalWeight > finalWeight) {
            calorieLimit += finalWeeklyWeightChangeTempo * 1000;
            proteinLimit = (0.2f * calorieLimit) / 4;
            fatLimit = (0.27f * calorieLimit) / 9;
            carbohydratesLimit = (0.53f * calorieLimit) / 4;
        } else if (finalGoalWeight < finalWeight) {
            calorieLimit -= finalWeeklyWeightChangeTempo * 1000;
            proteinLimit = (0.2f * calorieLimit) / 4;
            fatLimit = (0.25f * calorieLimit) / 9;
            carbohydratesLimit = (0.55f * calorieLimit) / 4;
        } else {
            proteinLimit = (0.2f * calorieLimit) / 4;
            fatLimit = (0.25f * calorieLimit) / 9;
            carbohydratesLimit = (0.55f * calorieLimit) / 4;
        }

        existing.setCalorieLimit(calorieLimit);
        existing.setProteinLimit(proteinLimit);
        existing.setFatLimit(fatLimit);
        existing.setCarbohydratesLimit(carbohydratesLimit);

        System.out.println(existing);

        BodyParameters updated = bodyParametersRepository.save(existing);

        BodyParametersResponse response = new BodyParametersResponse(updated.getUserId(), updated.getSex(),
                updated.getHeight(), updated.getWeight(), updated.getAge(), updated.getDailyActivityFactor(),
                updated.getDailyActivityTrainingFactor(), updated.getWeeklyWeightChangeTempo(), updated.getGoalWeight(),
                updated.getCalorieLimit(), updated.getProteinLimit(), updated.getFatLimit(),
                updated.getCarbohydratesLimit());

        return response;
    }

    @Transactional
    public BodyParameters addBodyParameters(Integer userId, Sex sex, Float height, Float weight, Integer age,
            Float dailyActivityFactor, Float dailyActivityTrainingFactor, Float weeklyWeightChangeTempo,
            Float goalWeight) {
        if (sex == null)
            throw new IllegalArgumentException("Sex must not be null");
        if (sex != Sex.female && sex != Sex.male)
            throw new IllegalArgumentException("Sex must be male or female");

        if (height == null || height <= 0)
            throw new IllegalArgumentException("Height must be greater than zero");
        if (weight == null || weight <= 0)
            throw new IllegalArgumentException("Weight must be greater than zero");
        if (age == null || age <= 0)
            throw new IllegalArgumentException("Age must be greater than zero");
        if (goalWeight == null || goalWeight <= 0)
            throw new IllegalArgumentException("Goal weight must be greater than zero");
        if (weeklyWeightChangeTempo == null || weeklyWeightChangeTempo < 0 || weeklyWeightChangeTempo > 1)
            throw new IllegalArgumentException("Weekly weight change tempo must be between 0 and 1");
        if (goalWeight.equals(weight)) {
            weeklyWeightChangeTempo = 0f;
        }
        if (dailyActivityFactor == null
                || (Float.compare(dailyActivityFactor, 0.7f) < 0 || Float.compare(dailyActivityFactor, 1.15f) > 0))
            throw new IllegalArgumentException("Daily activity factor must be between 0.7 and 1.15");
        if (dailyActivityTrainingFactor == null || (Float.compare(dailyActivityTrainingFactor, 0.5f) < 0
                || Float.compare(dailyActivityTrainingFactor, 0.95f) > 0))
            throw new IllegalArgumentException("Daily activity training factor must be between 0.5 and 0.95");

        float formula = 0;
        if (sex == Sex.male) {
            formula = (10 * weight) + (6.25f * height) - (5 * age) + 5;
        } else if (sex == Sex.female) {
            formula = (10 * weight) + (6.25f * height) - (5 * age) - 161;
        }

        float caloricZero = formula * (dailyActivityFactor + dailyActivityTrainingFactor);
        float calorieLimit = caloricZero;
        float proteinLimit;
        float fatLimit;
        float carbohydratesLimit;

        if (goalWeight > weight) {
            calorieLimit += weeklyWeightChangeTempo * 1000;
            proteinLimit = (0.2f * calorieLimit) / 4;
            fatLimit = (0.27f * calorieLimit) / 9;
            carbohydratesLimit = (0.53f * calorieLimit) / 4;
        } else if (goalWeight < weight) {
            calorieLimit -= weeklyWeightChangeTempo * 1000;
            proteinLimit = (0.2f * calorieLimit) / 4;
            fatLimit = (0.25f * calorieLimit) / 9;
            carbohydratesLimit = (0.55f * calorieLimit) / 4;
        } else {
            proteinLimit = (0.2f * calorieLimit) / 4;
            fatLimit = (0.25f * calorieLimit) / 9;
            carbohydratesLimit = (0.55f * calorieLimit) / 4;
        }

        BodyParameters bodyParameters = new BodyParameters();

        bodyParameters.setUserId(userId);
        bodyParameters.setSex(sex);
        bodyParameters.setHeight(height);
        bodyParameters.setWeight(weight);
        bodyParameters.setAge(age);
        bodyParameters.setDailyActivityFactor(dailyActivityFactor);
        bodyParameters.setDailyActivityTrainingFactor(dailyActivityTrainingFactor);
        bodyParameters.setWeeklyWeightChangeTempo(weeklyWeightChangeTempo);
        bodyParameters.setGoalWeight(goalWeight);
        bodyParameters.setCalorieLimit(calorieLimit);
        bodyParameters.setProteinLimit(proteinLimit);
        bodyParameters.setFatLimit(fatLimit);
        bodyParameters.setCarbohydratesLimit(carbohydratesLimit);

        return bodyParametersRepository.save(bodyParameters);
    }

    // admin
    public boolean updateStreak(int userId, int streak) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        user.setStreak(streak);
        userRepository.save(user);
        return true;
    }

    // admin
    public User updateStatus(int userId, Status status) {
        User user = getUserById(userId);
        user.setStatus(status);

        userRepository.save(user);
        return user;
    }

    // admin
    @Transactional
    public User updatePremiumExpiration(int userId, String dateString) {
        User user = getUserById(userId);
        LocalDate date;

        if (!user.getStatus().equals(Status.active))
            throw new IllegalArgumentException("User status has to be active");

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in format YYYY-MM-DD");
        }
        LocalDate now = LocalDate.now();

        if (date.isBefore(now))
            throw new IllegalArgumentException("Specified date must not be before now");

        user.setPremiumExpiration(date);

        return user;
    }

    // admin
    public void deleteUser(int userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
        System.out.println("User deleted successfully");
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public BodyParametersResponse getUserBodyParametersResponse(int userId) {
        BodyParameters bp = bodyParametersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("There are no body parameters for this user"));

        return new BodyParametersResponse(
                bp.getUserId(),
                bp.getSex(),
                bp.getHeight(),
                bp.getWeight(),
                bp.getAge(),
                bp.getDailyActivityFactor(),
                bp.getDailyActivityTrainingFactor(),
                bp.getWeeklyWeightChangeTempo(),
                bp.getGoalWeight(),
                bp.getCalorieLimit(),
                bp.getProteinLimit(),
                bp.getFatLimit(),
                bp.getCarbohydratesLimit());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void saveDeviceToken(int userId, String deviceToken) {
        List<UserDeviceToken> tokens = deviceTokenRepository.findByUserId(userId);

        if (!tokens.isEmpty()) {
            UserDeviceToken token = tokens.get(0);
            token.setDeviceToken(deviceToken);
            deviceTokenRepository.save(token);
        } else {
            UserDeviceToken newToken = new UserDeviceToken(userId, deviceToken);
            deviceTokenRepository.save(newToken);
        }
    }

    public List<String> getDeviceTokens(int userId) {
        return deviceTokenRepository.findByUserId(userId)
                .stream()
                .map(UserDeviceToken::getDeviceToken)
                .toList();
    }

    public List<User> getUsersByRecipientType(String recipients) {
        switch (recipients) {
            case "premium" -> {
                return userRepository.findByPremiumExpirationNotNull();
            }
            case "non_premium" -> {
                return userRepository.findByPremiumExpirationNull();
            }
            case "all" -> {
                return userRepository.findAll();
            }
            default ->
                throw new IllegalArgumentException("Invalid recipient type: " + recipients);
        }
    }

    @Transactional
    public String uploadProfileImage(int userId, MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        Path uploadPath = Paths.get("/app/uploads/profiles");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fileName = "profile_" + userId + ".jpg";
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        long ts = System.currentTimeMillis();

        String relativePath = "/images/profiles/" + fileName + "?t=" + ts;

        user.setImageUrl(relativePath);
        userRepository.save(user);

        return relativePath;
    }

    public List<Meal> getMyFavouriteMeals(int userId) {
        List<Favourite> favourites = favouriteRepository.findByUserId(userId);

        List<Meal> meals = favourites.stream()
                .map(f -> {
                    return mealRepository.findById(f.getMealId())
                            .orElse(null);
                })
                .filter(m -> m != null)
                .toList();

        return meals;
    }

    public Favourite addFavourite(int mealId, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Favourite> existing = favouriteRepository.findByUserIdAndMealId(user.getId(), mealId);
        if (existing.isPresent())
            throw new IllegalArgumentException("This meal is already in favourites");

        Favourite favourite = new Favourite();
        favourite.setUserId(user.getId());
        favourite.setMealId(mealId);

        Favourite saved = favouriteRepository.save(favourite);
        return saved;
    }

    public void removeFavourite(int mealId, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Favourite favourite = favouriteRepository.findByUserIdAndMealId(user.getId(), mealId)
                .orElseThrow(() -> new IllegalArgumentException("Favourite not found"));

        favouriteRepository.delete(favourite);
    }
}
