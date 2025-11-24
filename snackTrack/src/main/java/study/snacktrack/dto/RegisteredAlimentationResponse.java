package study.snacktrack.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import study.snacktrack.entities.RegisteredAlimentation;
import study.snacktrack.entities.enums.MealNames;

@Data
@NoArgsConstructor
public class RegisteredAlimentationResponse {

    private int id;
    private int userId;
    private EssentialFoodResponse essentialFood;

    // 🔹 ZMIANA: Z ApiFoodResponse na ApiFoodResponseDetailed
    private ApiFoodResponseDetailed mealApi;

    private MealResponse meal;

    private String timestamp;
    private Float amount;
    private Float pieces;
    private MealNames mealName;

    public RegisteredAlimentationResponse(RegisteredAlimentation entry) {
        this.id = entry.getId();
        this.userId = entry.getUserId();
        this.timestamp = entry.getTimestamp().toString();
        this.amount = entry.getAmount();
        this.pieces = entry.getPieces();
        this.mealName = entry.getMealName();

        if (entry.getEssentialFood() != null) {
            this.essentialFood = new EssentialFoodResponse(entry.getEssentialFood());
        }

        if (entry.getMeal() != null) {
            MealResponse mDto = new MealResponse();
            mDto.setId(entry.getMeal().getId());
            mDto.setName(entry.getMeal().getName());
            mDto.setDescription(entry.getMeal().getDescription());
            mDto.setAuthorId(entry.getMeal().getAuthorId());
            mDto.setImageUrl(entry.getMeal().getImageUrl());
            this.meal = mDto;
        }
    }
}