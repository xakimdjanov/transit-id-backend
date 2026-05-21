package com.autolitsenziya.api.service;

import com.autolitsenziya.api.entity.*;
import com.autolitsenziya.api.repository.LicenseRepository;
import com.autolitsenziya.api.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final LicenseRepository licenseRepository;
    private final LicenseService licenseService;

    public PaymentService(PaymentRepository paymentRepository, LicenseRepository licenseRepository, LicenseService licenseService) {
        this.paymentRepository = paymentRepository;
        this.licenseRepository = licenseRepository;
        this.licenseService = licenseService;
    }

    public Payment initiatePayment(UUID userId, UUID licenseId, BigDecimal amount, String method) {
        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new IllegalArgumentException("License not found!"));

        Payment payment = Payment.builder()
                .user(license.getDriver().getUser())
                .license(license)
                .amount(amount)
                .method(method)
                .status(PaymentStatus.PENDING)
                .transactionId("PENDING-" + UUID.randomUUID())
                .build();

        return paymentRepository.save(payment);
    }

    /**
     * Executes transaction settlement. Upon successful verification from Payment Webhook,
     * updates payment log to WAITING_FOR_APPROVAL status for admin review.
     */
    public Payment completePayment(String transactionId, PaymentStatus status) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction record not found!"));

        if (status == PaymentStatus.SUCCESS) {
            payment.setStatus(PaymentStatus.WAITING_FOR_APPROVAL);
        } else {
            payment.setStatus(status);
        }

        return paymentRepository.save(payment);
    }

    public Payment approvePayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found!"));

        if (payment.getStatus() != PaymentStatus.WAITING_FOR_APPROVAL) {
            throw new IllegalStateException("Only payments waiting for approval can be approved.");
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        License license = payment.getLicense();

        // Business Logic: Extend expiry_date by 1 year from the previous expiry date or today,
        // depending on if it has already expired.
        LocalDate currentExpiry = license.getExpiryDate();
        LocalDate today = LocalDate.now();
        LocalDate newExpiry = currentExpiry.isAfter(today)
                ? currentExpiry.plusYears(1)
                : today.plusYears(1);

        license.setExpiryDate(newExpiry);
        license.setStatus(licenseService.calculateStatus(newExpiry));
        licenseRepository.save(license);

        System.out.println("[BUSINESS LOGIC] Payment Approved. Extended license " +
                license.getLicenseNumber() + " expiry to: " + newExpiry);

        return paymentRepository.save(payment);
    }

    public Payment rejectPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found!"));

        if (payment.getStatus() != PaymentStatus.WAITING_FOR_APPROVAL) {
            throw new IllegalStateException("Only payments waiting for approval can be rejected.");
        }

        payment.setStatus(PaymentStatus.REJECTED);
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }
}
