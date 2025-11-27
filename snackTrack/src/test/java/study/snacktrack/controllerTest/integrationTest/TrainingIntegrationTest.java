package study.snacktrack.controllerTest.integrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
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
 * Integration tests for the TrainingController endpoints, verifying the user's interaction with training plans.
 * This test class covers the complete lifecycle of training assignment and removal using MockMvc against a real database context.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(properties = {"FIREBASE_CONFIG_JSON="})
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
     * Sets up the necessary state before each test execution, ensuring clean and consistent data.
     * It creates a test user and a persistent {@code TrainingInfo} record, then generates an authorization token for the user.
     */
    @BeforeEach
    @Transactional
    void setup() {
        userRepository.deleteAllInBatch();
        trainingInfoRepository.deleteAllInBatch();

        entityManager.clear();

        TrainingInfo training = new TrainingInfo();
        training.setName("Test Plan for assignment");
        training.setDescription("A rigorous test training plan.");
        training.setDurationTime(60);

        training = trainingInfoRepository.saveAndFlush(training);
        realTrainingId = training.getId();

        User user = new User();
        user.setEmail(TEST_USER_EMAIL);
        user.setPassword("TestPassword123!");
        user = userRepository.saveAndFlush(user);

        validAuthHeader = "Bearer " + jwtService.generateToken(user.getEmail(), "user");

        entityManager.clear();
    }

    /**
     * Tests the complete lifecycle of training management: Fetch -> Assign -> Verify Assignment -> Deprive -> Verify Deprivation.
     * This ensures the endpoints for listing, adding, and removing training plans operate correctly under authenticated conditions.
     * @throws Exception if any MockMvc operation fails
     */
    @Test
    void testAssignAndDepriveTrainingFlow() throws Exception {

        mockMvc.perform(get("/trainings")
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(realTrainingId));

        mockMvc.perform(post("/trainings/assign/" + realTrainingId)
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(content().string("Training successfully assigned to user"));

        mockMvc.perform(get("/trainings/my/details")
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingInfo.id").value(realTrainingId));

        mockMvc.perform(delete("/trainings/my/deprive")
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isOk())
                .andExpect(content().string("Training successfully deprived"));

        mockMvc.perform(get("/trainings/my/details")
                        .header("Authorization", validAuthHeader))
                .andExpect(status().isBadRequest());
    }
}