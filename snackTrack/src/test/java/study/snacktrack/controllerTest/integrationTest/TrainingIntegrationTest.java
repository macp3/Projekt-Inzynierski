package study.snacktrack.controllerTest.integrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import study.snacktrack.entities.User;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.services.JwtService;

import study.snacktrack.entities.TrainingInfo;
import study.snacktrack.repositories.TrainingInfoRepository;
import jakarta.persistence.EntityManager;

/**
 * Integration tests for the TrainingController endpoints.
 * This test verifies the complete lifecycle of training assignment and deprivation
 * using MockMvc, interacting with a real database context ({@code @ActiveProfiles("test")}).
 * It dynamically determines the {@code TrainingInfo} ID to avoid conflicts with
 * {@code GenerationType.IDENTITY}.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TrainingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TrainingInfoRepository trainingInfoRepository;

    @Autowired
    private EntityManager entityManager;

    private String validAuthHeader;
    private int realTrainingId;
    private static final String TEST_USER_EMAIL = "training@test.com";

    /**
     * Sets up the necessary state before each test execution:
     * 1. Cleans up relevant database tables (User and TrainingInfo).
     * 2. Inserts a new {@code TrainingInfo} entity and stores its generated ID.
     * 3. Creates a test user in the database.
     * 4. Generates a valid JWT for authorization.
     * 5. Clears the Hibernate session cache to ensure a clean state for MockMvc transactions.
     */
    @BeforeEach
    @Transactional
    void setup() {
        // Cleanup
        userRepository.deleteAllInBatch();
        trainingInfoRepository.deleteAllInBatch();

        // Clear Hibernate session cache to avoid StaleObjectStateException
        entityManager.clear();

        // 1. Insert TrainingInfo, allowing the database to assign the ID
        TrainingInfo training = new TrainingInfo();
        training.setName("Test Plan for assignment");
        training.setDescription("A rigorous test training plan.");
        training.setDurationTime(60);

        training = trainingInfoRepository.saveAndFlush(training);
        realTrainingId = training.getId(); // Store the actual generated ID

        // 2. Create a real User in the database
        User user = new User();
        user.setEmail(TEST_USER_EMAIL);
        user.setPassword("TestPassword123!");
        user = userRepository.saveAndFlush(user);

        // 3. Generate a valid token signed by JwtService
        validAuthHeader = "Bearer " + jwtService.generateToken(user.getEmail(), "user");

        // Final cleanup
        entityManager.clear();
    }

    /**
     * Tests the complete lifecycle of training management:
     * 1. Fetches available trainings.
     * 2. Assigns the dynamically created training to the user.
     * 3. Verifies the assignment details.
     * 4. Deprives (removes) the training from the user.
     * 5. Verifies the removal by expecting an error status on details retrieval.
     * @throws Exception if any MockMvc operation fails
     */
    @Test
    void testAssignAndDepriveTrainingFlow() throws Exception {

        // Step 1: Fetch all available trainings (Verify existence and authorization)
        mockMvc.perform(get("/trainings")
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(realTrainingId));

        // Step 2: Assign the training to the user
        mockMvc.perform(post("/trainings/assign/" + realTrainingId)
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(content().string("Training successfully assigned to user"));

        // Step 3: Check details (Verify assignment)
        mockMvc.perform(get("/trainings/my/details")
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingInfo.id").value(realTrainingId));

        // Step 4: Deprive (remove) the training from the user
        mockMvc.perform(delete("/trainings/my/deprive")
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(content().string("Training successfully deprived"));

        // Step 5: Verification after removal (Expect an error/not found status)
        mockMvc.perform(get("/trainings/my/details")
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isBadRequest());
    }
}