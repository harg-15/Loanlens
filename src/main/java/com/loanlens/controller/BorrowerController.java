package com.loanlens.controller;

import com.loanlens.dto.request.BorrowerRequest;
import com.loanlens.dto.response.BorrowerResponse;
import com.loanlens.service.BorrowerService;
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
@RequestMapping("/api/borrowers")
@RequiredArgsConstructor
@Tag(name = "Borrowers", description = "Borrower management")
@SecurityRequirement(name = "bearerAuth")
public class BorrowerController {

    private final BorrowerService borrowerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    @Operation(summary = "Onboard a new borrower")
    public ResponseEntity<BorrowerResponse> createBorrower(@Valid @RequestBody BorrowerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowerService.createBorrower(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "List all borrowers")
    public ResponseEntity<List<BorrowerResponse>> getAllBorrowers() {
        return ResponseEntity.ok(borrowerService.getAllBorrowers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Get borrower by ID")
    public ResponseEntity<BorrowerResponse> getBorrower(@PathVariable Long id) {
        return ResponseEntity.ok(borrowerService.getBorrowerById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    @Operation(summary = "Update borrower details")
    public ResponseEntity<BorrowerResponse> updateBorrower(
            @PathVariable Long id,
            @Valid @RequestBody BorrowerRequest request) {
        return ResponseEntity.ok(borrowerService.updateBorrower(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a borrower (Admin only)")
    public ResponseEntity<Void> deleteBorrower(@PathVariable Long id) {
        borrowerService.deleteBorrower(id);
        return ResponseEntity.noContent().build();
    }
}