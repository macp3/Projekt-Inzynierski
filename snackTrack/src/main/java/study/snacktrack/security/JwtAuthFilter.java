package study.snacktrack.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import study.snacktrack.services.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import study.snacktrack.entities.Admin;
import study.snacktrack.entities.User;
import study.snacktrack.repositories.AdminRepository;
import study.snacktrack.repositories.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filter responsible for processing JWT authentication for every incoming request.
 * It ensures that authenticated users (Admins or Users) are properly set in the Spring Security context.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    /**
     * Performs filtering logic to validate the JWT from the request header and set up authentication.
     * This method extracts the token, verifies the email and account type, and constructs an authentication object.
     *
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param filterChain The filter chain.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

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
                UsernamePasswordAuthenticationToken authToken
                        = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}