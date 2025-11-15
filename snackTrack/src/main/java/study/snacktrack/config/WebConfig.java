package study.snacktrack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // foldery w kontenerze podpięte z volume
        registry.addResourceHandler("/images/profiles/**")
                .addResourceLocations("file:/app/uploads/profiles/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/images/meals/**")
                .addResourceLocations("file:/app/uploads/meals/")
                .setCachePeriod(3600);
    }
}
