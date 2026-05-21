package com.autolitsenziya.api.service;

import com.autolitsenziya.api.dto.LicenseRequest;
import com.autolitsenziya.api.entity.DriverProfile;
import com.autolitsenziya.api.entity.License;
import com.autolitsenziya.api.entity.LicenseStatus;
import com.autolitsenziya.api.repository.DriverProfileRepository;
import com.autolitsenziya.api.repository.LicenseRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final DriverProfileRepository driverProfileRepository;

    public LicenseService(LicenseRepository licenseRepository, DriverProfileRepository driverProfileRepository) {
        this.licenseRepository = licenseRepository;
        this.driverProfileRepository = driverProfileRepository;
    }

    public License createLicense(LicenseRequest request) {
        DriverProfile driver = driverProfileRepository.findById(request.driverId())
                .orElseThrow(() -> new IllegalArgumentException("Driver profile not found!"));

        LicenseStatus status = calculateStatus(request.expiryDate());

        License license = License.builder()
                .driver(driver)
                .licenseNumber(request.licenseNumber())
                .issueDate(request.issueDate())
                .expiryDate(request.expiryDate())
                .status(status)
                .qrCodeUrl("https://auto-litsenziya.uz/verify/" + request.licenseNumber())
                .build();

        return licenseRepository.save(license);
    }

    public Optional<License> getLicenseById(UUID id) {
        return licenseRepository.findById(id);
    }

    public Optional<License> getLicenseByNumber(String licenseNumber) {
        return licenseRepository.findByLicenseNumber(licenseNumber);
    }

    public Optional<License> getLicenseByDriverProfileId(UUID driverId) {
        return licenseRepository.findTopByDriverIdOrderByExpiryDateDesc(driverId);
    }

    public Optional<License> cancelLatestLicenseForDriver(UUID driverId) {
        return getLicenseByDriverProfileId(driverId)
                .map(license -> {
                    LocalDate expiredDate = LocalDate.now().minusDays(1);
                    license.setExpiryDate(expiredDate);
                    license.setStatus(LicenseStatus.EXPIRED);
                    return licenseRepository.save(license);
                });
    }

    public Optional<License> renewLatestLicenseForDriver(UUID driverId) {
        return getLicenseByDriverProfileId(driverId)
                .map(license -> {
                    LocalDate now = LocalDate.now();
                    LocalDate currentExpiry = license.getExpiryDate();
                    LocalDate newExpiry = currentExpiry.isBefore(now)
                            ? now.plusYears(1)
                            : currentExpiry.plusYears(1);

                    license.setExpiryDate(newExpiry);
                    license.setStatus(calculateStatus(newExpiry));
                    return licenseRepository.save(license);
                });
    }

    public List<License> getLicensesByDriver(UUID userId) {
        return licenseRepository.findByDriverUserId(userId);
    }

    public LicenseStatus calculateStatus(LocalDate expiryDate) {
        LocalDate today = LocalDate.now();
        if (expiryDate.isBefore(today)) {
            return LicenseStatus.EXPIRED;
        }

        long daysLeft = ChronoUnit.DAYS.between(today, expiryDate);
        if (daysLeft <= 15) {
            return LicenseStatus.EXPIRING;
        }

        return LicenseStatus.ACTIVE;
    }

    /**
     * Daily automated CRON job running at midnight to re-evaluate all license statuses
     * in the PostgreSQL database and flag warnings accordingly.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void evaluateAllLicenses() {
        System.out.println("[CRON SCHEDULER] Daily license status lifecycle check started.");
        List<License> licenses = licenseRepository.findAll();
        for (License license : licenses) {
            LicenseStatus newStatus = calculateStatus(license.getExpiryDate());
            if (license.getStatus() != newStatus) {
                license.setStatus(newStatus);
                licenseRepository.save(license);
                System.out.println("[CRON SCHEDULER] License " + license.getLicenseNumber() + " status updated to: " + newStatus);
            }
        }
    }
}
