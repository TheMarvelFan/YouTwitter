package com.mthree.backend.services;

import javax.crypto.SecretKey;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mthree.backend.models.User;
import com.mthree.backend.services.interfaces.JWTService;
import com.mthree.backend.utils.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JWTServiceImpl implements JWTService {
    @Value("${security.jwt.secret}")
    private String key;

    @Value("${security.jwt.refresh_secret}")
    private String refreshKey;

    @Value("${security.jwt.expiration}")
    private long expirationTime;

    @Value("${security.jwt.refresh_expiration}")
    private long refreshExpirationTime;

//    private String encodedSecretKey;
//    private String encodedRefreshKey;
//
//    @PostConstruct
//    public void init() {
//        this.encodedSecretKey = Base64.getEncoder().encodeToString(this.key.getBytes());
//        this.encodedRefreshKey = Base64.getEncoder().encodeToString(this.refreshKey.getBytes());
//    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (this.expirationTime * 1000)))
                .and()
                .signWith(getAccessKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (this.refreshExpirationTime * 1000)))
                .and()
                .signWith(getRefreshKey())
                .compact();
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken, User user) {
        try {
            String username = extractUsernameFromRefreshToken(refreshToken);
            if (username != null && username.equals(user.getUsername())
                    && !isRefreshTokenExpired(refreshToken)) {
                return generateAccessToken(user);
            }
        } catch (Exception e) {
            throw new ErrorType(
                    400,
                    "Invalid refresh token OOGA BOOGA " + e.getMessage()
            );
        }
        return null;
    }

    private SecretKey getAccessKey() {
//        byte[] keyBytes = Base64.getDecoder().decode(this.key.getBytes());
        return Keys.hmacShaKeyFor(this.key.getBytes());
    }

    private SecretKey getRefreshKey() {
//        byte[] keyBytes = Base64.getDecoder().decode(this.refreshKey.getBytes());
        return Keys.hmacShaKeyFor(this.refreshKey.getBytes());
    }

    public String extractUsername(String token) {
        System.out.println("Extracting username from token: " + token);
        return extractClaim(token, Claims::getSubject, getAccessKey());
    }

    public String extractUsernameFromRefreshToken(String token) {
        return extractClaim(token, Claims::getSubject, getRefreshKey());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, SecretKey key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails, String type) {
        if (type.equals("access")) {
            String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } else if (type.equals("refresh")) {
            String username = extractUsernameFromRefreshToken(token);
            return (username.equals(userDetails.getUsername()) && !isRefreshTokenExpired(token));
        } else {
            throw new ErrorType(400, "Invalid token type");
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token, getAccessKey()).before(new Date());
    }

    private boolean isRefreshTokenExpired(String token) {
        return extractExpiration(token, getRefreshKey()).before(new Date());
    }

    private Date extractExpiration(String token, SecretKey key) {
        return extractClaim(token, Claims::getExpiration, key);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractExpiration(token, getAccessKey());
    }
}

