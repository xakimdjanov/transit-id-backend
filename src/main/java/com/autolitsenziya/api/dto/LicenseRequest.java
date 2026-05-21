package com.autolitsenziya.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record LicenseRequest(
    @NotBlank(message = "License number is required")
    String licenseNumber,

    @NotNull(message = "Issue date is required")
    LocalDate issueDate,

    @NotNull(message = "Expiry date is required")
    LocalDate expiryDate,

    @NotNull(message = "Driver profile ID is required")
    UUID driverId
) {}
