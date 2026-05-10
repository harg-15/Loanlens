package com.loanlens.controller;

import com.loanlens.dto.response.PortfolioSummaryResponse;
import com.loanlens.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
@Tag(name = "Portfolio Analytics", description = "Portfolio-level risk metrics and snapshots")
@SecurityRequirement(name = "bearerAuth")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/latest")
    @PreAuthorize("hasAnyRole('RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Get the latest portfolio snapshot")
    public ResponseEntity<PortfolioSummaryResponse> getLatestSnapshot() {
        return ResponseEntity.ok(portfolioService.getLatestSnapshot());
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('RISK_ANALYST', 'ADMIN')")
    @Operation(summary = "Get last 30 daily portfolio snapshots (trend data)")
    public ResponseEntity<List<PortfolioSummaryResponse>> getHistory() {
        return ResponseEntity.ok(portfolioService.getLast30Snapshots());
    }
}
