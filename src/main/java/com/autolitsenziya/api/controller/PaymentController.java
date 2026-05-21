package com.autolitsenziya.api.controller;

import com.autolitsenziya.api.entity.Payment;
import com.autolitsenziya.api.entity.PaymentStatus;
import com.autolitsenziya.api.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> initiatePayment(
            @RequestParam UUID userId,
            @RequestParam UUID licenseId,
            @RequestParam BigDecimal amount,
            @RequestParam String method) {
        
        Payment payment = paymentService.initiatePayment(userId, licenseId, amount, method);
        return ResponseEntity.ok(payment);
    }

    /**
     * Mobile integration: Direct payment simulation.
     * Marks payment as WAITING_FOR_APPROVAL.
     */
    @PostMapping("/simulate")
    public ResponseEntity<?> simulatePayment(
            @RequestParam UUID userId,
            @RequestParam UUID licenseId,
            @RequestParam BigDecimal amount,
            @RequestParam String method) {
        
        Payment payment = paymentService.initiatePayment(userId, licenseId, amount, method);
        Payment completed = paymentService.completePayment(payment.getTransactionId(), PaymentStatus.SUCCESS);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "transactionId", completed.getTransactionId(),
                "status", completed.getStatus().name(),
                "message", "Payment initiated. Waiting for admin approval."
        ));
    }

    /**
     * Admin Endpoint: List all payments waiting for approval.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Payment>> getPendingPayments() {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(PaymentStatus.WAITING_FOR_APPROVAL));
    }

    /**
     * Admin Endpoint: Approve payment and extend license.
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Payment> approvePayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.approvePayment(id));
    }

    /**
     * Admin Endpoint: Reject payment.
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<Payment> rejectPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.rejectPayment(id));
    }

    /**
     * Webhook endpoint simulation for Click billing system callback.
     * Completes transactions and triggers license extensions on the backend.
     */
    @PostMapping("/webhook/click")
    public ResponseEntity<?> handleClickWebhook(
            @RequestParam String transactionId,
            @RequestParam String status) {
        
        PaymentStatus paymentStatus = status.equalsIgnoreCase("success") 
                ? PaymentStatus.SUCCESS 
                : PaymentStatus.FAILED;

        Payment payment = paymentService.completePayment(transactionId, paymentStatus);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "transactionId", payment.getTransactionId(),
                "status", payment.getStatus().name(),
                "message", payment.getStatus() == PaymentStatus.WAITING_FOR_APPROVAL ? "Waiting for admin approval" : "Payment failed"
        ));
    }

    /**
     * Webhook endpoint simulation for Payme billing system callback.
     */
    @PostMapping("/webhook/payme")
    public ResponseEntity<?> handlePaymeWebhook(
            @RequestParam String transactionId,
            @RequestParam String status) {
        
        PaymentStatus paymentStatus = status.equalsIgnoreCase("success") 
                ? PaymentStatus.SUCCESS 
                : PaymentStatus.FAILED;

        Payment payment = paymentService.completePayment(transactionId, paymentStatus);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "transactionId", payment.getTransactionId(),
                "status", payment.getStatus().name()
        ));
    }
}
