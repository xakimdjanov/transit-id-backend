package com.autolitsenziya.api.controller;

import com.autolitsenziya.api.dto.AuthResponse;
import com.autolitsenziya.api.dto.OtpRequest;
import com.autolitsenziya.api.dto.OtpVerifyRequest;
import com.autolitsenziya.api.dto.RefreshTokenRequest;
import com.autolitsenziya.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody OtpRequest request) {
        String code = authService.requestOtp(request.phone());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Verification code sent successfully!",
                "code", code
        ));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        AuthResponse response = authService.verifyOtpAndAuthenticate(request.phone(), request.code());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }
}
