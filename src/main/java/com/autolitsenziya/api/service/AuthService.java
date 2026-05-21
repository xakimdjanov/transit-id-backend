package com.autolitsenziya.api.service;

import com.autolitsenziya.api.dto.AuthResponse;
import com.autolitsenziya.api.entity.Role;
import com.autolitsenziya.api.entity.User;
import com.autolitsenziya.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;

    public AuthService(UserRepository userRepository, OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    public String requestOtp(String phone) {
        String code = otpService.generateOtp(phone);

        User user = userRepository.findByPhone(phone)
                .orElseGet(() -> userRepository.save(User.builder()
                        .phone(phone)
                        .otpCode(code)
                        .role(Role.DRIVER) // default signup role
                        .build()));

        if (user.getOtpCode() == null || !user.getOtpCode().equals(code)) {
            user.setOtpCode(code);
            userRepository.save(user);
        }

        return code;
    }

    public AuthResponse verifyOtpAndAuthenticate(String phone, String code) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("Telefon raqami topilmadi."));

        String trimmedCode = code == null ? null : code.trim();
        String storedCode = user.getOtpCode() == null ? null : user.getOtpCode().trim();
        System.out.println("[OTP VERIFY] phone=" + phone + " requestCode='" + trimmedCode + "' storedCode='" + storedCode + "'");

        boolean isValid = otpService.validateOtp(trimmedCode, storedCode);
        if (!isValid) {
            throw new IllegalArgumentException("Incorrect verification code!");
        }

        user.setOtpCode(null);

        // Create standard mock JWT tokens for API communication
        String accessToken = "mock-jwt-access-token-" + UUID.randomUUID();
        String refreshToken = "mock-jwt-refresh-token-" + UUID.randomUUID();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getPhone(),
                user.getRole().name()
        );
    }

    public AuthResponse refreshAccessToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        String accessToken = "mock-jwt-access-token-" + UUID.randomUUID();
        String newRefreshToken = "mock-jwt-refresh-token-" + UUID.randomUUID();
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new AuthResponse(
                accessToken,
                newRefreshToken,
                user.getId(),
                user.getPhone(),
                user.getRole().name()
        );
    }
}
