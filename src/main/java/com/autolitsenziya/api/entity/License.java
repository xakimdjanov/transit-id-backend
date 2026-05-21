package com.autolitsenziya.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "licenses")
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverProfile driver;

    @Column(name = "license_number", unique = true, nullable = false, length = 30)
    private String licenseNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private LicenseStatus status;

    @Column(name = "qr_code_url")
    private String qrCodeUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public License() {}

    public License(UUID id, DriverProfile driver, String licenseNumber, LocalDate issueDate, LocalDate expiryDate, LicenseStatus status, String qrCodeUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driver = driver;
        this.licenseNumber = licenseNumber;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.qrCodeUrl = qrCodeUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public DriverProfile getDriver() { return driver; }
    public void setDriver(DriverProfile driver) { this.driver = driver; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public LicenseStatus getStatus() { return status; }
    public void setStatus(LicenseStatus status) { this.status = status; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Manual Builder
    public static LicenseBuilder builder() {
        return new LicenseBuilder();
    }

    public static class LicenseBuilder {
        private UUID id;
        private DriverProfile driver;
        private String licenseNumber;
        private LocalDate issueDate;
        private LocalDate expiryDate;
        private LicenseStatus status;
        private String qrCodeUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public LicenseBuilder id(UUID id) { this.id = id; return this; }
        public LicenseBuilder driver(DriverProfile driver) { this.driver = driver; return this; }
        public LicenseBuilder licenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; return this; }
        public LicenseBuilder issueDate(LocalDate issueDate) { this.issueDate = issueDate; return this; }
        public LicenseBuilder expiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; return this; }
        public LicenseBuilder status(LicenseStatus status) { this.status = status; return this; }
        public LicenseBuilder qrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; return this; }
        public LicenseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public LicenseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public License build() {
            return new License(id, driver, licenseNumber, issueDate, expiryDate, status, qrCodeUrl, createdAt, updatedAt);
        }
    }
}
