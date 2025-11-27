package study.snacktrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class for the SnackTrack application, responsible for bootstrapping the
 * Spring Boot context.
 * The {@code @SpringBootApplication} annotation combines configuration,
 * component scanning, and auto-configuration, while {@code @EnableScheduling}
 * enables support for scheduled tasks within the application.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SnackTrackApplication {

    /**
     * The main method which uses Spring Boot's {@code SpringApplication.run()}
     * method to launch the application.
     * This static method starts the embedded Tomcat server and sets up the entire
     * Spring environment.
     * 
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(SnackTrackApplication.class, args);
    }

}