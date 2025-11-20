package study.snacktrack.entities;

import java.time.LocalDate;

import jakarta.persistence.*;
import study.snacktrack.entities.enums.MealNames;

@Entity
@Table(name = "registered_alimentation")
public class RegisteredAlimentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "essential_id")
    private EssentialFood essentialFood;

    @Column(name = "meal_api_id")
    private Integer mealApiId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @Column(name = "timestamp", nullable = false)
    private LocalDate timestamp;
    @Column(name = "meal", nullable = false)
    @Enumerated(EnumType.STRING)
    private MealNames mealName;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "pieces")
    private Float pieces;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EssentialFood getEssentialFood() {
        return essentialFood;
    }

    public void setEssentialFood(EssentialFood essentialFood) {
        this.essentialFood = essentialFood;
    }

    public Integer getMealApiId() {
        return mealApiId;
    }

    public void setMealApiId(Integer mealApiId) {
        this.mealApiId = mealApiId;
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

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public MealNames getMealName() {
        return mealName;
    }

    public void setMealName(MealNames mealName) {
        this.mealName = mealName;
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

    public Integer getUserId() {
        return userId;
    }
}
