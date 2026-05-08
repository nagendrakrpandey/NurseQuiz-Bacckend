package Tech.Nagendra.NurseQuiz.Utitlty;

import Tech.Nagendra.NurseQuiz.Entity.user;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "nagendra-secret-key-nurse-quiz-2026-nagendra-secure-key"; // 🔥 >= 32 chars
    private final long EXPIRATION = 1000 * 60 * 60; // 1 hour

    // 🔑 Generate Key
    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ==============================
    // 🔥 GENERATE TOKEN
    // ==============================
    public String generateToken(user user) {

        if (user.getId() == null) {
            throw new RuntimeException("User ID is NULL ❌");
        }

        // ✅ ROLE LOGIC (IMPORTANT)
        String role;
        if (user.getRoleId() != null && user.getRoleId() == 1) {
            role = "ADMIN";
        } else if (user.getRoleId() != null && user.getRoleId() == 3) {
            role = "USER";
        } else {
            role = "USER"; // default
        }

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("roleId", user.getRoleId())   // optional but useful
                .claim("role", role)                // ✅ IMPORTANT
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ==============================
    // 🔥 CLEAN TOKEN
    // ==============================
    public String cleanToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    // ==============================
    // 🔥 EXTRACT ALL CLAIMS
    // ==============================
    private Claims extractAllClaims(String token) {

        token = cleanToken(token);

        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ==============================
    // 🔥 EXTRACT EMAIL
    // ==============================
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ==============================
    // 🔥 EXTRACT USER ID (SAFE)
    // ==============================
    public Long extractUserId(String token) {

        Object userId = extractAllClaims(token).get("userId");

        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        } else if (userId instanceof Long) {
            return (Long) userId;
        }

        return null;
    }

    // ==============================
    // 🔥 EXTRACT ROLE
    // ==============================
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // ==============================
    // 🔥 VALIDATE TOKEN
    // ==============================
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;

        } catch (ExpiredJwtException e) {
            System.out.println(" Token Expired");
        } catch (UnsupportedJwtException e) {
            System.out.println(" Unsupported Token");
        } catch (MalformedJwtException e) {
            System.out.println(" Invalid Token");
        } catch (SignatureException e) {
            System.out.println(" Signature Invalid");
        } catch (IllegalArgumentException e) {
            System.out.println(" Token Empty");
        }

        return false;
    }
}