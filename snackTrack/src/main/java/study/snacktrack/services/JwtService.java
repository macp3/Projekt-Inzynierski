package study.snacktrack.services;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Service responsible for handling JWT token generation and extraction.
 */
@Service
public class JwtService {

    private static final String SECRET_KEY = "supersecretkey123supersecretkey123";

    /**
     * Generates a JWT token with email and account type claims.
     *
     * @param email user email
     * @param accountType type of account
     * @return signed JWT token
     */
    public String generateToken(String email, String accountType) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", accountType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts email (subject) from JWT token.
     *
     * @param token JWT token
     * @return email stored in token
     */
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extracts account type claim from JWT token.
     *
     * @param token JWT token
     * @return account type stored in token
     */
    public String extractAccountType(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("type");
    }
}
