package study.snacktrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import study.snacktrack.entities.Admin;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.AdminRepository;
import study.snacktrack.repositories.UserRepository;
import study.snacktrack.services.JwtService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Filter responsible for processing JWT authentication for every incoming
 * request.
 * Contains safety checks to ensure CORS headers are preserved even on auth
 * failure.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);
            final String email = jwtService.extractEmail(token);
            final String type = jwtService.extractAccountType(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Object principal = null;
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

                if ("ADMIN".equals(type)) {
                    Admin admin = adminRepository.findByEmail(email).orElse(null);
                    if (admin != null) {
                        principal = admin;
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }
                } else {
                    User user = userRepository.findByEmail(email).orElse(null);
                    if (user != null) {
                        principal = user;
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    }
                }

                if (principal != null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}