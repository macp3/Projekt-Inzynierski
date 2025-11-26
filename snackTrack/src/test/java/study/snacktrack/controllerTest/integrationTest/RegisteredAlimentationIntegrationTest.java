package study.snacktrack.controllerTest.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import study.snacktrack.entities.User;
import study.snacktrack.entities.EssentialFood;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.repositories.FoodRepository;
import study.snacktrack.services.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the RegisteredAlimentationController endpoints.
 * This class uses a real database and mock web environment to test the full stack
 * from controller to repository.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RegisteredAlimentationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodRepository foodRepository;

    private String validToken;
    private int realFoodId;
    private int realUserId;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        foodRepository.deleteAll();

        User user = new User();
        user.setEmail("integration@test.com");
        user.setPassword("Password123!");
        user = userRepository.save(user);
        realUserId = user.getId();

        validToken = "Bearer " + jwtService.generateToken(user.getEmail(), "user");

        EssentialFood food = new EssentialFood();
        food.setName("Test Egg");
        food.setCalories(150);
        food.setProtein(12.0f);
        food.setCarbohydrates(1.0f);
        food.setFat(10.0f);
        food.setDescription("Test product description");
        food.setAuthorId(realUserId);

        food = foodRepository.save(food);
        realFoodId = food.getId();
    }

    /**
     * Tests the complete flow: adding a food entry, fetching the entry to get its ID,
     * deleting the entry, and verifying that the entry is gone.
     * @throws Exception if any MVC operation fails
     */
    @Test
    void testAddEntryFetchAndDeleteCycle() throws Exception {

        String requestDate = "2024-01-20";
        String mealName = "breakfast";

        // Step 1: Add a new entry
        String addEntryRequest = String.format(
                "{\"essentialId\":%d, \"amount\":200.0, \"mealName\":\"%s\"}",
                realFoodId, mealName
        );

        mockMvc.perform(post("/registered/add")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addEntryRequest)
                        .param("date", requestDate))
                .andExpect(status().isOk());

        // Step 2: Fetch entries to retrieve the database-assigned ID
        String getResponse = mockMvc.perform(get("/registered/my")
                        .header("Authorization", validToken)
                        .param("date", requestDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].essentialFood.id").value(realFoodId))
                .andExpect(jsonPath("$[0].mealName").value(mealName))
                .andReturn().getResponse().getContentAsString();

        JsonNode rootNode = objectMapper.readTree(getResponse);
        int entryIdToDelete = rootNode.get(0).get("id").asInt();

        System.out.println("Found entry ID for deletion: " + entryIdToDelete);

        // Step 3: Delete the entry using the retrieved ID
        mockMvc.perform(delete("/registered/delete/" + entryIdToDelete)
                        .header("Authorization", validToken))
                .andExpect(status().isNoContent());

        // Step 4: Verify the entry has been deleted
        mockMvc.perform(get("/registered/my")
                        .header("Authorization", validToken)
                        .param("date", requestDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}