package study.snacktrack.config;

import study.snacktrack.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Wyłączenie CSRF (poprawne dla REST API)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 🔥 KLUCZOWA ZMIANA: Włączenie CORS w Security
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin-auth/**").permitAll()
                        .requestMatchers("/images/**", "/uploads/**").permitAll()

                        // Opcja OPTIONS jest potrzebna, aby przeglądarka mogła "zapytać" o uprawnienia
                        // przed wysłaniem tokena
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().hasRole("USER"))

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 3. 🔥 Definicja reguł CORS dla Spring Security
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Zezwól na frontend Reacta (localhost:3000)
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // Zezwól na wszystkie metody HTTP
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Zezwól na wszystkie nagłówki (w tym Authorization!)
        configuration.setAllowedHeaders(List.of("*")); // Lub jawnie: "Authorization", "Content-Type", "Cache-Control"

        // Zezwól na ciasteczka/credentials (jeśli używasz)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}