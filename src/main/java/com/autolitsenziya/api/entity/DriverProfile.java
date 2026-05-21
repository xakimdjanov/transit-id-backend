package com.autolitsenziya.api.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "drivers")
public class DriverProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "car_brand", nullable = false)
    private String carBrand;

    @Column(name = "car_model", nullable = false)
    private String carModel;

    @Column(name = "car_color", nullable = false)
    private String carColor;

    @Column(name = "car_number", nullable = false, length = 15)
    private String carNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public DriverProfile() {}

    public DriverProfile(UUID id, User user, String fullName, String carBrand, String carModel, String carColor, String carNumber, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.fullName = fullName;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carColor = carColor;
        this.carNumber = carNumber;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCarBrand() { return carBrand; }
    public void setCarBrand(String carBrand) { this.carBrand = carBrand; }

    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }

    public String getCarColor() { return carColor; }
    public void setCarColor(String carColor) { this.carColor = carColor; }

    public String getCarNumber() { return carNumber; }
    public void setCarNumber(String carNumber) { this.carNumber = carNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Manual Builder
    public static DriverProfileBuilder builder() {
        return new DriverProfileBuilder();
    }

    public static class DriverProfileBuilder {
        private UUID id;
        private User user;
        private String fullName;
        private String carBrand;
        private String carModel;
        private String carColor;
        private String carNumber;
        private LocalDateTime createdAt;

        public DriverProfileBuilder id(UUID id) { this.id = id; return this; }
        public DriverProfileBuilder user(User user) { this.user = user; return this; }
        public DriverProfileBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public DriverProfileBuilder carBrand(String carBrand) { this.carBrand = carBrand; return this; }
        public DriverProfileBuilder carModel(String carModel) { this.carModel = carModel; return this; }
        public DriverProfileBuilder carColor(String carColor) { this.carColor = carColor; return this; }
        public DriverProfileBuilder carNumber(String carNumber) { this.carNumber = carNumber; return this; }
        public DriverProfileBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public DriverProfile build() {
            return new DriverProfile(id, user, fullName, carBrand, carModel, carColor, carNumber, createdAt);
        }
    }
}
