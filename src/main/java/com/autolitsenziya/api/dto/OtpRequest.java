package com.autolitsenziya.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpRequest(
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+998\\d{9}$", message = "Phone number must match format +998XXXXXXXXX")
    String phone
) {}
