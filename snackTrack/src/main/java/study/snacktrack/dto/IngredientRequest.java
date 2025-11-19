package study.snacktrack.dto;

public class IngredientRequest {

    private Integer essentialFoodId;
    private Integer essentialApiId;
    private Float amount;
    private Float pieces;
    private String defaultUnit;

    public Integer getEssentialFoodId() {
        return essentialFoodId;
    }

    public void setEssentialFoodId(Integer essentialFoodId) {
        this.essentialFoodId = essentialFoodId;
    }

    public Integer getEssentialApiId() {
        return essentialApiId;
    }

    public void setEssentialApiId(Integer essentialApiId) {
        this.essentialApiId = essentialApiId;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }

    public void setDefaultUnit(String defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    public Float getPieces() {
        return pieces;
    }

    public void setPieces(Float pieces) {
        this.pieces = pieces;
    }
}
