package com.autolitsenziya.api.dto;

import java.util.UUID;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    UUID userId,
    String phone,
    String role
) {}
