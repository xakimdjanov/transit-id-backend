package com.autolitsenziya.api.repository;

import com.autolitsenziya.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhone(String phone);
    Optional<User> findByRefreshToken(String refreshToken);
    boolean existsByPhone(String phone);
}
