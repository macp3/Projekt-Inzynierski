package study.snacktrack.config;

import lombok.RequiredArgsConstructor;
import study.snacktrack.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // Wyłączamy domyślną obsługę CORS w łańcuchu, bo obsłużymy ją "Custom Filterem"
                // poniżej
                // lub zostawiamy .cors(Customizer.withDefaults()) jeśli nie używamy
                // FilterRegistrationBean,
                // ale poniższa metoda z FilterRegistrationBean jest pewniejsza.
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Zezwalamy na OPTIONS jawnie (dla pewności)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**", "/admin-auth/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().hasRole("USER"))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * TO JEST KLUCZ DO ROZWIĄZANIA PROBLEMU.
     * Rejestrujemy filtr CORS z najwyższym priorytetem.
     * Dzięki temu CORS jest obsługiwany ZANIM Spring Security zablokuje cokolwiek.
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Używamy patterns, co pozwala na elastyczność (http vs https, subdomeny)
        // To zadziała zarówno na localhost jak i na produkcji Railway
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "https://*.railway.app", // Obsługuje wszystkie subdomeny na railway
                frontendUrl // Dodatkowo wartość ze zmiennej środowiskowej
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*")); // Zezwól na wszystkie nagłówki (Authorization, Content-Type
                                                       // itp.)
        configuration.setAllowCredentials(true); // Wymagane jeśli przesyłasz cookies lub Authorization header
        configuration.setMaxAge(3600L); // Cache'uj ustawienia CORS przez godzinę, żeby przeglądarka nie pytała co
                                        // chwilę

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        CorsFilter corsFilter = new CorsFilter(source);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(corsFilter);

        // Ustawiamy najwyższy priorytet - filtr wykona się jako pierwszy!
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}