package com.mthree.backend.services.interfaces;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

import com.mthree.backend.models.User;

public interface JWTService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String generateAccessTokenFromRefreshToken(String refreshToken, User user);
    String extractUsername(String token);
    boolean validateToken(String token, UserDetails userDetails, String type);
    String extractUsernameFromRefreshToken(String token);
    Date extractExpiration(String token);
}
