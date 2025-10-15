package com.example.demo.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.entities.Admin;
import com.example.demo.entities.User;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

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
