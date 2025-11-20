package study.snacktrack.dto;

import java.time.LocalDate;

import study.snacktrack.entities.Meal;
import study.snacktrack.entities.RegisteredAlimentation;
import study.snacktrack.entities.enums.MealNames;

public class RegisteredAlimentationResponse {

    private int id;
    private int userId;
    private EssentialFoodResponse essentialFood;
    private ApiFoodResponseDetailed mealApi;
    private Meal meal;
    private LocalDate timestamp;
    private MealNames mealName;
    private Float amount;
    private Float pieces;

    public RegisteredAlimentationResponse() {
    }

    public RegisteredAlimentationResponse(RegisteredAlimentation ra) {
        this.id = ra.getId();
        this.userId = ra.getUserId();
        this.timestamp = ra.getTimestamp();
        this.amount = ra.getAmount();
        this.pieces = ra.getPieces();
        this.meal = ra.getMeal();
        this.mealName = ra.getMealName();

        if (ra.getEssentialFood() != null) {
            this.essentialFood = new EssentialFoodResponse(ra.getEssentialFood());
        }
    }

    // gettery i settery
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public EssentialFoodResponse getEssentialFood() {
        return essentialFood;
    }

    public void setEssentialFood(EssentialFoodResponse essentialFood) {
        this.essentialFood = essentialFood;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getPieces() {
        return pieces;
    }

    public void setPieces(Float pieces) {
        this.pieces = pieces;
    }

    public void setMealApi(ApiFoodResponseDetailed foodFromApiById) {
        this.mealApi = foodFromApiById;
    }

    public ApiFoodResponseDetailed getMealApi() {
        return mealApi;
    }

    public MealNames getMealName() {
        return mealName;
    }

    public void setMealName(MealNames mealName) {
        this.mealName = mealName;
    }
}
