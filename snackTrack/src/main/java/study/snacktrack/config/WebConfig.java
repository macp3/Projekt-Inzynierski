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
        registry.addResourceHandler("/images/profiles/**")
                .addResourceLocations("file:///app/uploads/profiles/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/images/meals/**")
                .addResourceLocations("file:///app/uploads/meals/")
                .setCachePeriod(3600);
    }
}