package com.loanlens.controller;

import com.loanlens.dto.request.LoanRequest;
import com.loanlens.dto.request.LoanUpdateRequest;
import com.loanlens.dto.response.InstallmentResponse;
import com.loanlens.dto.response.LoanResponse;
import com.loanlens.enums.LoanStatus;
import com.loanlens.enums.LoanType;
import com.loanlens.enums.RiskTier;
import com.loanlens.service.LoanService;
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
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Loan creation and portfolio management")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    @Operation(summary = "Create a new loan (auto-generates EMI schedule)")
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Get loan details by ID")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Filter loans by type, status, risk tier, or assigned officer")
    public ResponseEntity<List<LoanResponse>> getLoans(
            @RequestParam(required = false) LoanType loanType,
            @RequestParam(required = false) LoanStatus status,
            @RequestParam(required = false) RiskTier riskTier,
            @RequestParam(required = false) Long officerId) {
        return ResponseEntity.ok(loanService.getLoansWithFilters(loanType, status, riskTier, officerId));
    }

    @GetMapping("/borrower/{borrowerId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Get all loans for a borrower")
    public ResponseEntity<List<LoanResponse>> getLoansByBorrower(@PathVariable Long borrowerId) {
        return ResponseEntity.ok(loanService.getLoansByBorrower(borrowerId));
    }

    @GetMapping("/{loanId}/installments")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Get EMI installment schedule for a loan")
    public ResponseEntity<List<InstallmentResponse>> getInstallments(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.getLoanInstallments(loanId));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    @Operation(summary = "Update editable loan fields (status, collateral, assigned officer)")
    public ResponseEntity<LoanResponse> updateLoan(@PathVariable Long id, @RequestBody LoanUpdateRequest request) {
        return ResponseEntity.ok(loanService.updateLoan(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a loan (only if it has no recorded repayments)")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}
