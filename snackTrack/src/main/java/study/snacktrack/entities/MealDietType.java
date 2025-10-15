package study.snacktrack.entities;

import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "meal_diet_types")
public class MealDietType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int id;
    @Column(name = "meal_id")
    private int mealId;
    @Column(name = "diet_type_id")
    private int dietTypeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public int getDietTypeId() {
        return dietTypeId;
    }

    public void setDietTypeId(int dietTypeId) {
        this.dietTypeId = dietTypeId;
    }
}
