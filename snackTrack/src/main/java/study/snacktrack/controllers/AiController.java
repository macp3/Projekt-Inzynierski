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

            String response = callGeminiJson(finalPrompt);

            return ResponseEntity.ok(response);
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

        // Add User Instruction
        if (instruction != null && !instruction.trim().isEmpty()) {
            sb.append("The user specifically requests: ").append(instruction).append("\n");
        } else {
            sb.append("Generate a general daily shopping list based on the provided products.\n");
        }

        // Strict JSON Rules
        sb.append("\nOUTPUT RULES:\n");
        sb.append("1. Return ONLY valid JSON.\n");
        sb.append("2. Format: A JSON Array of objects.\n");
        sb.append("3. Object keys: \"name\", \"description\", \"quantity\".\n");
        sb.append("4. Do not use Markdown formatting.");

        return sb.toString();
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
            // Simple error handling
            throw new RuntimeException("AI Service unavailable: " + e.getMessage());
        }
    }
}