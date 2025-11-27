package study.snacktrack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global configuration for Spring Web MVC settings.
 * Customizes resource handling and Cross-Origin Resource Sharing (CORS) rules.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures static resource handlers for serving uploaded files.
     * Maps virtual paths (e.g., /images/**) to physical storage locations.
     *
     * @param registry The registry for adding resource handlers.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Handler for user profile images, mapped to the Docker volume mount point.
        registry.addResourceHandler("/images/profiles/**")
                .addResourceLocations("file:/app/uploads/profiles/")
                .setCachePeriod(3600);

        // Handler for meal images, mapped to the Docker volume mount point.
        registry.addResourceHandler("/images/meals/**")
                .addResourceLocations("file:/app/uploads/meals/")
                .setCachePeriod(3600);
    }

    /**
     * Configures global CORS (Cross-Origin Resource Sharing) mappings.
     * Allows requests from the specified frontend origin (http://localhost:3000) for all paths.
     *
     * @param registry The registry for adding CORS mappings.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}