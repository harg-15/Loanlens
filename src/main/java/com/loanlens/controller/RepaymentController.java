package com.loanlens.controller;

import com.loanlens.dto.request.RepaymentRequest;
import com.loanlens.dto.response.RepaymentResponse;
import com.loanlens.service.RepaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repayments")
@RequiredArgsConstructor
@Tag(name = "Repayments", description = "Record and retrieve loan repayments")
@SecurityRequirement(name = "bearerAuth")
public class RepaymentController {

    private final RepaymentService repaymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    @Operation(summary = "Record a repayment against an installment")
    public ResponseEntity<RepaymentResponse> recordRepayment(@Valid @RequestBody RepaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repaymentService.recordRepayment(request));
    }

    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Get repayment history for a loan")
    public ResponseEntity<List<RepaymentResponse>> getRepaymentHistory(@PathVariable Long loanId) {
        return ResponseEntity.ok(repaymentService.getLoanRepaymentHistory(loanId));
    }
}
