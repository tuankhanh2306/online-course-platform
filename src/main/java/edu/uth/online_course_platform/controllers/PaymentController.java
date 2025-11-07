package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.response.ApiResponse;
import edu.uth.online_course_platform.dto.response.PaymentResponse;
import edu.uth.online_course_platform.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/simulate-success/{enrollmentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> simulateSuccess(@PathVariable Long enrollmentId) {
        PaymentResponse res = paymentService.simulatePaymentSuccess(enrollmentId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Payment simulated success", res));
    }

}
