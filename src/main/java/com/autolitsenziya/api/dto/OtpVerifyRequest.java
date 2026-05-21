package com.autolitsenziya.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpVerifyRequest(
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+998\\d{9}$", message = "Phone number must match format +998XXXXXXXXX")
    String phone,

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^\\d{4,6}$", message = "Verification code must be 4 to 6 digits")
    String code
) {}
