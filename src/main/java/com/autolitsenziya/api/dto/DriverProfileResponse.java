package com.autolitsenziya.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record DriverProfileResponse(
    UUID id,
    UUID userId,
    String phone,
    String fullName,
    String carBrand,
    String carModel,
    String carColor,
    String carNumber,
    String licenseNumber,
    LocalDate expiryDate,
    String status,
    LocalDateTime createdAt
) {}