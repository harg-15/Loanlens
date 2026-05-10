package com.loanlens.controller;

import com.loanlens.dto.response.RiskAlertResponse;
import com.loanlens.enums.AlertStatus;
import com.loanlens.service.RiskAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.loanlens.exception.BadRequestException;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "Risk Alerts", description = "View and manage risk escalation alerts")
@SecurityRequirement(name = "bearerAuth")
public class RiskAlertController {

    private final RiskAlertService riskAlertService;

    @GetMapping
    @PreAuthorize("hasAnyRole('RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Get all risk alerts (Risk Analyst / Admin)")
    public ResponseEntity<List<RiskAlertResponse>> getAllAlerts(
            @RequestParam(required = false) AlertStatus status) {
        if (status != null) {
            return ResponseEntity.ok(riskAlertService.getAlertsByStatus(status));
        }
        return ResponseEntity.ok(riskAlertService.getAllAlerts());
    }

    @GetMapping("/officer/{officerId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Get alerts assigned to a loan officer")
    public ResponseEntity<List<RiskAlertResponse>> getAlertsByOfficer(@PathVariable Long officerId) {
        return ResponseEntity.ok(riskAlertService.getAlertsByOfficer(officerId));
    }

    @GetMapping("/officer/{officerId}/open")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Get open alerts for a loan officer")
    public ResponseEntity<List<RiskAlertResponse>> getOpenAlertsByOfficer(@PathVariable Long officerId) {
        return ResponseEntity.ok(riskAlertService.getOpenAlertsByOfficer(officerId));
    }

    @GetMapping("/loan/{loanId}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Get alert history for a loan")
    public ResponseEntity<List<RiskAlertResponse>> getAlertsByLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(riskAlertService.getAlertsByLoan(loanId));
    }

    @PatchMapping("/{alertId}/status")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'ADMIN')")
    @Operation(summary = "Update alert status (acknowledge or resolve)")
    public ResponseEntity<RiskAlertResponse> updateAlertStatus(
            @PathVariable Long alertId,
            @RequestBody Map<String, String> body) {
        String rawStatus = body.get("status");
        if (rawStatus == null || rawStatus.isBlank()) {
            throw new BadRequestException("Field 'status' is required");
        }
        AlertStatus status;
        try {
            status = AlertStatus.valueOf(rawStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status value: " + rawStatus + ". Allowed: OPEN, ACKNOWLEDGED, RESOLVED");
        }
        String notes = body.get("notes");
        return ResponseEntity.ok(riskAlertService.updateAlertStatus(alertId, status, notes));
    }
}
