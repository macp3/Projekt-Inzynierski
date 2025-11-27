package study.snacktrack.dto;

import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object (DTO) used for sending a request to the AI shopping list generation service.
 * This class encapsulates the user's specific dietary or shopping goal along with their current product context.
 */
@Data
public class AiShoppingRequest {

    /**
     * Represents the specific command or dietary instruction provided by the user.
     * This prompt guides the AI in generating a tailored shopping list suggestion.
     */
    private String prompt;

    /**
     * Contains a list of food items that are known to the user's local database.
     * This context helps the AI understand the user's existing inventory or preferred products for generating relevant suggestions.
     */
    private List<String> productContext;
}