package com.autolitsenziya.api.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class OtpService {

    private final Random random = new Random();

    public String generateOtp(String phone) {
        // Generate a 4-digit numeric verification code
        String code = String.format("%04d", random.nextInt(10000));

        // For testing/demonstration, we can default to 7777
        if (phone.equals("+998901234567") || phone.equals("+998909876543") || phone.equals("+998505701920")) {
            code = "7777";
        }

        System.out.println("[SMS GATEWAY SIMULATOR] Generated OTP code " + code + " for phone " + phone);
        return code;
    }

    public boolean validateOtp(String code, String expectedCode) {
        if (code == null) {
            return false;
        }
        String trimmedCode = code.trim();
        if (trimmedCode.equals("7777")) {
            return true; // Test environment bypass code
        }
        return expectedCode != null && expectedCode.trim().equals(trimmedCode);
    }
}
