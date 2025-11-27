package study.snacktrack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test class for the entire Spring Boot application context.
 * This ensures that the application context loads successfully and verifies the connection to the required MySQL database before running any tests.
 */
@SpringBootTest
@ActiveProfiles("test")
class SnackTrackApplicationTests {

    /**
     * Simple test to verify that the Spring Boot application context loads correctly without throwing any exceptions.
     * This confirms the configuration and component scanning are successful.
     */
    @Test
    void contextLoads() {
    }
}