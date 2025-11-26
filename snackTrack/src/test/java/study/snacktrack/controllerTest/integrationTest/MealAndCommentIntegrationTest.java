package study.snacktrack.controllerTest.integrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Required imports for JWT and User setup
import study.snacktrack.entities.User;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.services.JwtService;

/**
 * Integration test for the Meal and Comment workflow.
 * This test verifies the complete flow:
 * 1. Meal creation (including mandatory ingredients).
 * 2. Comment addition to the created meal.
 * 3. 'Liking' the comment by the author.
 * 4. Final verification of the like count and like status.
 * * It ensures the application's business logic, data persistence, and
 * REST API responses are correctly integrated.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MealAndCommentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private String validAuthHeader;
    private static final String TEST_USER_EMAIL = "mealtest@test.com";

    /**
     * The request body for creating a meal.
     * Includes a minimal valid ingredient to satisfy {@code MealService} validation
     * (requires at least one ingredient with a food source ID and amount/pieces).
     */
    private static final String MEAL_REQUEST =
            "{\"name\":\"Savory Oatmeal\", \"description\":\"Perfect after workout\","
                    + "\"ingredients\":[{\"essentialApiId\":123, \"amount\":50.0}]}";

    /**
     * Template for the comment creation request, expecting the meal ID.
     */
    private static final String COMMENT_REQUEST_TEMPLATE =
            "{\"mealId\":%d, \"content\":\"Great idea! I must try this.\"}";

    /**
     * Sets up the necessary state before each test execution:
     * 1. Cleans up user data.
     * 2. Creates a test user with mock data (Name/Surname needed for {@code CommentResponse} formatting).
     * 3. Generates a valid JWT for authorization used in all subsequent requests.
     */
    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        // 1. Create a real User
        User user = new User();
        user.setEmail(TEST_USER_EMAIL);
        user.setPassword("TestPassword123!");
        user.setName("Test");
        user.setSurname("User");
        user = userRepository.save(user);

        // 2. Generate a valid token
        validAuthHeader = "Bearer " + jwtService.generateToken(user.getEmail(), "user");
    }

    /**
     * Executes the four-step integration flow: Create Meal, Add Comment, Like Comment, and Verify.
     * * @throws Exception if any MockMvc operation fails or JSON parsing fails.
     */
    @Test
    void testMealAndCommentFlow() throws Exception {

        // Step 1: Create a new meal (POST /meals/create)
        // Expected response body is a simple Integer (the meal ID).
        MvcResult mealResult = mockMvc.perform(post("/meals/create")
                        .header("Authorization", validAuthHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MEAL_REQUEST))
                .andExpect(status().isOk())
                .andReturn();

        // EXTRACT ID of the created meal (Parsed as a simple Integer string)
        String mealResponseJson = mealResult.getResponse().getContentAsString();
        int createdMealId = Integer.parseInt(mealResponseJson);

        // Fail-safe check for the extracted ID
        if (createdMealId <= 0) {
            throw new AssertionError("Meal ID extracted from response was not positive: " + createdMealId);
        }


        // Step 2: Add a comment to the newly created meal (POST /comments/add)
        String finalCommentRequest = String.format(COMMENT_REQUEST_TEMPLATE, createdMealId);
        MvcResult commentResult = mockMvc.perform(post("/comments/add")
                        .header("Authorization", validAuthHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(finalCommentRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists()) // Expecting a CommentResponse DTO object
                .andReturn();

        // EXTRACT ID of the created comment (Parsed from CommentResponse DTO)
        String commentResponseJson = commentResult.getResponse().getContentAsString();
        JsonNode commentNode = objectMapper.readTree(commentResponseJson);
        int createdCommentId = commentNode.get("id").asInt();


        // Step 3: Like the comment (POST /comments/{id}/like)
        mockMvc.perform(post("/comments/" + createdCommentId + "/like")
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(content().string("Like toggled"));


        // Step 4: Verification (Fetch comments for the meal - GET /comments/meal/{mealId})
        mockMvc.perform(get("/comments/meal/" + createdMealId)
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Verify the returned list contains the comment with the correct ID
                .andExpect(jsonPath("$[0].id").value(createdCommentId))
                // Verify the like state for the current logged-in user
                .andExpect(jsonPath("$[0].isLiked").value(true))
                // Verify the total count
                .andExpect(jsonPath("$[0].likesCount").value(1));
    }
}