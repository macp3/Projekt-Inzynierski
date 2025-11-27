package study.snacktrack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test class for the entire Spring Boot application context.
 * This ensures that the application context loads successfully and verifies the connection to the required MySQL database before running any tests.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class SnackTrackApplicationTests {

    /**
     * Attempts to establish a connection to the MySQL database specified by the test configuration.
     * This method retries the connection up to 10 times with a 2-second delay to ensure the database container or service is fully operational before tests begin.
     * @throws InterruptedException if the thread is interrupted while waiting
     * @throws IllegalStateException if the MySQL database is not ready after all retries
     */
    @BeforeAll
    void waitForDatabase() throws InterruptedException {
        int retries = 10;
        while (retries > 0) {
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/snackTrackDB",
                    "myuser",
                    "secret")) {
                return;
            } catch (SQLException e) {
                retries--;
                Thread.sleep(2000);
            }
        }
        throw new IllegalStateException("MySQL not ready");
    }

    /**
     * Simple test to verify that the Spring Boot application context loads correctly without throwing any exceptions.
     * This confirms the configuration and component scanning are successful.
     */
    @Test
    void contextLoads() {
    }
}