package com.autolitsenziya.api.controller;

import com.autolitsenziya.api.dto.DriverProfileResponse;
import com.autolitsenziya.api.entity.DriverProfile;
import com.autolitsenziya.api.entity.License;
import com.autolitsenziya.api.entity.User;
import com.autolitsenziya.api.repository.DriverProfileRepository;
import com.autolitsenziya.api.repository.LicenseRepository;
import com.autolitsenziya.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/drivers")
@CrossOrigin(origins = "*")
public class DriverProfileController {

    private final DriverProfileRepository driverProfileRepository;
    private final UserRepository userRepository;
    private final LicenseRepository licenseRepository;

    public DriverProfileController(DriverProfileRepository driverProfileRepository, UserRepository userRepository, LicenseRepository licenseRepository) {
        this.driverProfileRepository = driverProfileRepository;
        this.userRepository = userRepository;
        this.licenseRepository = licenseRepository;
    }

    @GetMapping
    public ResponseEntity<List<DriverProfileResponse>> getAllProfiles() {
        List<DriverProfileResponse> profiles = driverProfileRepository.findAllWithUser().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DriverProfileResponse>> getAllProfilesAlias() {
        return getAllProfiles();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getProfileByUserId(@PathVariable UUID userId) {
        return driverProfileRepository.findByUserId(userId)
                .map(profile -> ResponseEntity.ok(Map.of(
                        "success", true,
                        "profile", Map.of(
                                "id", profile.getId(),
                                "fullName", profile.getFullName(),
                                "carBrand", profile.getCarBrand(),
                                "carModel", profile.getCarModel(),
                                "carColor", profile.getCarColor(),
                                "carNumber", profile.getCarNumber(),
                                "phone", profile.getUser().getPhone(),
                                "createdAt", profile.getCreatedAt()
                        )
                )))
                .orElse(ResponseEntity.ok(Map.of("success", false, "message", "Profile not found")));
    }

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody Map<String, String> request) {
        UUID userId = UUID.fromString(request.get("userId"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String fullName = request.get("fullName");
        String carBrand = request.get("carBrand");
        String carModel = request.get("carModel");
        String carColor = request.get("carColor");
        String carNumber = request.get("carNumber");

        DriverProfile profile = driverProfileRepository.findByUserId(userId)
                .map(existing -> {
                    existing.setFullName(fullName);
                    existing.setCarBrand(carBrand);
                    existing.setCarModel(carModel);
                    existing.setCarColor(carColor);
                    existing.setCarNumber(carNumber);
                    return existing;
                })
                .orElseGet(() -> DriverProfile.builder()
                        .user(user)
                        .fullName(fullName)
                        .carBrand(carBrand)
                        .carModel(carModel)
                        .carColor(carColor)
                        .carNumber(carNumber)
                        .build());

        DriverProfile saved = driverProfileRepository.save(profile);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "profileId", saved.getId()
        ));
    }

    private DriverProfileResponse mapToResponse(DriverProfile profile) {
        License latestLicense = licenseRepository.findByDriverUserId(profile.getUser().getId()).stream()
                .max(Comparator.comparing(License::getExpiryDate))
                .orElse(null);

        return new DriverProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getPhone(),
                profile.getFullName(),
                profile.getCarBrand(),
                profile.getCarModel(),
                profile.getCarColor(),
                profile.getCarNumber(),
                latestLicense != null ? latestLicense.getLicenseNumber() : "",
                latestLicense != null ? latestLicense.getExpiryDate() : null,
                latestLicense != null ? latestLicense.getStatus().name() : "Expired",
                profile.getCreatedAt()
        );
    }
}
