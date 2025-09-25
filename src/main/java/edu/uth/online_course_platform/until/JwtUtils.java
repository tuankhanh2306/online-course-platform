package edu.uth.online_course_platform.until;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    // Khóa bí mật để ký token (nên lưu trong application.properties trong thực tế)

    private String secretKey;

    private long expiration;

    @Autowired
    public JwtUtils(
            @Value("${jwt.secret:ThisIsMySuperSecretKeyForJWT1234567890a}") String secretKey,
            @Value("${jwt.expiration:86400000}") long expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    // Tạo Key từ secret string
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) { // 32 bytes = 256 bits
            throw new JwtException("Secret key length must be at least 256 bits");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 👉 Method 1: generate token từ email và role
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, email);
    }

    // 👉 Method 2: generate token từ UserDetails
    public String generateToken(UserDetails userDetails) {
        String email = userDetails.getUsername();
        // Loại bỏ tiền tố "ROLE_"
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .collect(Collectors.joining(","));  // Nếu có nhiều role
        return generateToken(email, roles);
    }

    // Tạo token với claims và subject (email)
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Kiểm tra token hợp lệ
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Trích xuất email
    public String extractEmail(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    // Trích xuất role
    public String extractRole(String token) {

        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Trích xuất claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}