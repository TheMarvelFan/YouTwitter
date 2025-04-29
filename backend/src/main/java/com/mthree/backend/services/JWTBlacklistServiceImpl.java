package com.mthree.backend.services;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.mthree.backend.services.interfaces.JWTBlacklistService;

@Service
public class JWTBlacklistServiceImpl implements JWTBlacklistService {
    private final ConcurrentHashMap<String, Date> blacklistedTokens;

    public JWTBlacklistServiceImpl() {
        this.blacklistedTokens = new ConcurrentHashMap<>();
    }

    // Add token to blacklist with expiration time
    public void blacklistToken(String token, Date expiryDate) {
        blacklistedTokens.put(token, expiryDate);
    }

    // Check if token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        if (!blacklistedTokens.containsKey(token)) {
            return false;
        }

        // Clean up expired tokens when checking
        Date now = new Date();
        if (blacklistedTokens.get(token).before(now)) {
            blacklistedTokens.remove(token);
            return false;
        }

        return true;
    }

    // Cleanup method (can be scheduled to run periodically)
    public void cleanupExpiredTokens() {
        Date now = new Date();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().before(now));
    }
}
