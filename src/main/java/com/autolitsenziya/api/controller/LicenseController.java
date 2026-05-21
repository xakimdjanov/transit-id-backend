package com.autolitsenziya.api.controller;

import com.autolitsenziya.api.dto.LicenseRequest;
import com.autolitsenziya.api.dto.LicenseResponse;
import com.autolitsenziya.api.entity.License;
import com.autolitsenziya.api.service.LicenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/licenses")
@CrossOrigin(origins = "*")
public class LicenseController {

    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @PostMapping
    public ResponseEntity<LicenseResponse> registerLicense(@Valid @RequestBody LicenseRequest request) {
        License license = licenseService.createLicense(request);
        return ResponseEntity.ok(mapToResponse(license));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LicenseResponse> getLicenseById(@PathVariable UUID id) {
        return licenseService.getLicenseById(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/driver/{userId}")
    public ResponseEntity<List<LicenseResponse>> getLicensesByDriver(@PathVariable UUID userId) {
        List<LicenseResponse> licenses = licenseService.getLicensesByDriver(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(licenses);
    }

    // Public GAI verification search endpoint
    @GetMapping("/verify/{licenseNumber}")
    public ResponseEntity<?> verifyLicenseNumber(@PathVariable String licenseNumber) {
        return licenseService.getLicenseByNumber(licenseNumber)
                .map(license -> ResponseEntity.ok(java.util.Map.ofEntries(
                        java.util.Map.entry("success", true),
                        java.util.Map.entry("id", license.getId()),
                        java.util.Map.entry("licenseNumber", license.getLicenseNumber()),
                        java.util.Map.entry("status", license.getStatus().name()),
                        java.util.Map.entry("expiryDate", license.getExpiryDate().toString()),
                        java.util.Map.entry("driverName", license.getDriver().getFullName()),
                        java.util.Map.entry("driverPhone", license.getDriver().getUser().getPhone()),
                        java.util.Map.entry("carBrand", license.getDriver().getCarBrand()),
                        java.util.Map.entry("carModel", license.getDriver().getCarModel()),
                        java.util.Map.entry("carColor", license.getDriver().getCarColor()),
                        java.util.Map.entry("carNumber", license.getDriver().getCarNumber())
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/driver/{driverId}/cancel")
    public ResponseEntity<LicenseResponse> cancelLicense(@PathVariable UUID driverId) {
        return licenseService.cancelLatestLicenseForDriver(driverId)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/driver/{driverId}/renew")
    public ResponseEntity<LicenseResponse> renewLicense(@PathVariable UUID driverId) {
        return licenseService.renewLatestLicenseForDriver(driverId)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private LicenseResponse mapToResponse(License license) {
        return new LicenseResponse(
                license.getId(),
                license.getDriver().getId(),
                license.getLicenseNumber(),
                license.getIssueDate(),
                license.getExpiryDate(),
                license.getStatus(),
                license.getQrCodeUrl()
        );
    }
}
