package com.mthree.backend.services.interfaces;

import java.util.Date;

public interface JWTBlacklistService {
    void blacklistToken(String token, Date expiryDate);

    boolean isTokenBlacklisted(String token);

    void cleanupExpiredTokens();
}
