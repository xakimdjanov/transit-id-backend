package com.autolitsenziya.api.repository;

import com.autolitsenziya.api.entity.License;
import com.autolitsenziya.api.entity.LicenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LicenseRepository extends JpaRepository<License, UUID> {
    Optional<License> findByLicenseNumber(String licenseNumber);
    List<License> findByDriverUserId(UUID userId);
    Optional<License> findTopByDriverIdOrderByExpiryDateDesc(UUID driverId);
    List<License> findByExpiryDateLessThanEqualAndStatusIn(LocalDate date, List<LicenseStatus> statuses);
}
