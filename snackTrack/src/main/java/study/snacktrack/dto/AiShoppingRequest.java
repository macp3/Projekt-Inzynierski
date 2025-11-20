package study.snacktrack.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiShoppingRequest {
    // The command from the user (e.g., "I want a high protein diet")
    // Can be null if they just want a standard list.
    private String prompt;

    // The list of items from the phone's local database
    // e.g., ["Milk", "Eggs", "Bread", "Chicken"]
    private List<String> productContext;
}