package com.autolitsenziya.api.repository;

import com.autolitsenziya.api.entity.DriverProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, UUID> {
    Optional<DriverProfile> findByUserId(UUID userId);

    @EntityGraph(attributePaths = {"user"})
    @Query("select d from DriverProfile d")
    List<DriverProfile> findAllWithUser();
}
