package study.snacktrack.controllers;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import study.snacktrack.dto.AiShoppingRequest;
import study.snacktrack.services.JwtService;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final JwtService jwtService;

    @PostMapping("/shopping-list/generate")
    public ResponseEntity<String> generateShoppingList(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AiShoppingRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            jwtService.extractEmail(token);

            List<String> phoneData = request.getProductContext();
            String userInstruction = request.getPrompt();

            String finalPrompt = buildShoppingPrompt(phoneData, userInstruction);
            String rawResponse = callGeminiJson(finalPrompt);

            String cleanResponse = cleanGeminiResponse(rawResponse);

            return ResponseEntity.ok(cleanResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/diet/generate")
    public ResponseEntity<String> generateDietPlan(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AiShoppingRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            jwtService.extractEmail(token);

            String userInstruction = request.getPrompt();

            String finalPrompt = buildDietPrompt(userInstruction);
            String rawResponse = callGeminiJson(finalPrompt);

            String cleanResponse = cleanGeminiResponse(rawResponse);

            return ResponseEntity.ok(cleanResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String buildShoppingPrompt(List<String> products, String instruction) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a smart shopping assistant.\n");

        if (products != null && !products.isEmpty()) {
            sb.append("Here is a list of products the user likes or currently has: [")
                    .append(String.join(", ", products))
                    .append("]. Use this to understand their preferences.\n");
        }

        if (instruction != null && !instruction.trim().isEmpty()) {
            sb.append("The user specifically requests: ").append(instruction).append("\n");
        } else {
            sb.append("Generate a general daily shopping list based on the provided products.\n");
        }

        sb.append("\nOUTPUT RULES:\n");
        sb.append("1. Return ONLY valid JSON.\n");
        sb.append("2. Format: A JSON Array of objects.\n");
        sb.append("3. Object keys: \"name\", \"description\", \"quantity\".\n");
        sb.append("4. Do not use Markdown formatting.");

        return sb.toString();
    }

    private String buildDietPrompt(String instruction) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a professional dietician.\n");
        sb.append("Create a 1-day meal plan (Breakfast, Lunch, Dinner, Supper, Snack).\n");

        if (instruction != null && !instruction.trim().isEmpty()) {
            sb.append("User goal/preference: ").append(instruction).append("\n");
        }

        sb.append("\nCRITICAL OUTPUT RULES:\n");
        sb.append("1. Return ONLY valid JSON array.\n");
        sb.append(
                "2. JSON Format: [ { \"meal\": \"Breakfast\", \"productName\": \"Oatmeal\", \"amount\": 100, \"unit\": \"g\" }, ... ]\n");
        sb.append("3. 'meal' keys MUST be strictly one of: 'Breakfast', 'Lunch', 'Dinner', 'Supper', 'Snack'.\n");
        sb.append(
                "4. 'productName' should be a simple English name for food database search (e.g. 'Banana', 'Chicken Breast', 'Rice').\n");
        sb.append("5. 'unit' should be 'g' (grams) or 'ml' or 'piece'. If 'piece', use it for fruits/eggs etc.\n");
        sb.append("6. 'amount' is a number.\n");
        sb.append("7. Do not use Markdown code blocks (no ```json). Just raw JSON.\n");

        return sb.toString();
    }

    private String cleanGeminiResponse(String text) {
        if (text == null)
            return "[]";

        int startIndex = text.indexOf("[");
        int endIndex = text.lastIndexOf("]");

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return text.substring(startIndex, endIndex + 1);
        }

        return text;
    }

    public String callGeminiJson(String prompt) {
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .build();

        try (Client client = Client.builder().apiKey(apiKey).build()) {
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    config);
            return response.text();
        } catch (Exception e) {
            throw new RuntimeException("AI Service unavailable: " + e.getMessage());
        }
    }
}