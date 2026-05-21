package com.autolitsenziya.api.dto;

import com.autolitsenziya.api.entity.LicenseStatus;

import java.time.LocalDate;
import java.util.UUID;

public record LicenseResponse(
    UUID id,
    UUID driverId,
    String licenseNumber,
    LocalDate issueDate,
    LocalDate expiryDate,
    LicenseStatus status,
    String qrCodeUrl
) {}
