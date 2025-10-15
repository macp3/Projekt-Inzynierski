package study.snacktrack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class SnackTrackApplicationTests {

    @BeforeAll
    void waitForDatabase() throws InterruptedException {
        int retries = 10;
        while (retries > 0) {
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/mydatabase",
                    "myuser",
                    "secret")) {
                return; // połączenie OK
            } catch (SQLException e) {
                retries--;
                Thread.sleep(2000); // czekaj 2 sekundy
            }
        }
        throw new IllegalStateException("MySQL not ready");
    }

    @Test
    void contextLoads() {
        // test kontekstu Spring Boot
    }
}
