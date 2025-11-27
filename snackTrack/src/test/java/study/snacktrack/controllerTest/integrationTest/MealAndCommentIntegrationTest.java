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
import org.springframework.transaction.annotation.Transactional;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.services.JwtService;

/**
 * Integration test for the Meal and Comment workflow, verifying the complete process from creation to interaction.
 * This test ensures that the application's business logic, data persistence, and REST API responses are correctly integrated.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
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
     * The request body for creating a meal, including a minimal valid ingredient.
     */
    private static final String MEAL_REQUEST =
            "{\"name\":\"Savory Oatmeal\", \"description\":\"Perfect after workout\","
                    + "\"ingredients\":[{\"essentialApiId\":123, \"amount\":50.0}]}";

    /**
     * Template for the comment creation request, parameterized to accept the meal ID.
     */
    private static final String COMMENT_REQUEST_TEMPLATE =
            "{\"mealId\":%d, \"content\":\"Great idea! I must try this.\"}";

    /**
     * Sets up the necessary state before each test execution.
     * It creates a test user and generates a valid JWT token required for authorization in subsequent requests.
     */
    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User user = new User();
        user.setEmail(TEST_USER_EMAIL);
        user.setPassword("TestPassword123!");
        user.setName("Test");
        user.setSurname("User");
        user = userRepository.save(user);

        validAuthHeader = "Bearer " + jwtService.generateToken(user.getEmail(), "user");
    }

    /**
     * Executes the four-step integration flow: Create Meal, Add Comment, Like Comment, and final Verification.
     * This method tests the integration of the Meal, Comment, and Like endpoints sequentially.
     *
     * @throws Exception if any MockMvc operation fails or JSON parsing fails.
     */
    @Test
    void testMealAndCommentFlow() throws Exception {

        MvcResult mealResult = mockMvc.perform(post("/meals/create")
                        .header("Authorization", validAuthHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MEAL_REQUEST))
                .andExpect(status().isOk())
                .andReturn();

        String mealResponseJson = mealResult.getResponse().getContentAsString();
        int createdMealId = Integer.parseInt(mealResponseJson);

        if (createdMealId <= 0) {
            throw new AssertionError("Meal ID extracted from response was not positive: " + createdMealId);
        }


        String finalCommentRequest = String.format(COMMENT_REQUEST_TEMPLATE, createdMealId);
        MvcResult commentResult = mockMvc.perform(post("/comments/add")
                        .header("Authorization", validAuthHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(finalCommentRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists()) // Expecting a CommentResponse DTO object
                .andReturn();

        String commentResponseJson = commentResult.getResponse().getContentAsString();
        JsonNode commentNode = objectMapper.readTree(commentResponseJson);
        int createdCommentId = commentNode.get("id").asInt();


        mockMvc.perform(post("/comments/" + createdCommentId + "/like")
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(content().string("Like toggled"));


        mockMvc.perform(get("/comments/meal/" + createdMealId)
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(createdCommentId))
                .andExpect(jsonPath("$[0].isLiked").value(true))
                .andExpect(jsonPath("$[0].likesCount").value(1));
    }
}