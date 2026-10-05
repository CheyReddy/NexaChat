package com.cwebworks.chat.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {}
