package com.loanlens.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PortfolioSummaryResponse {
    private Long id;
    private LocalDate snapshotDate;
    private Long totalActiveLoans;
    private Long npaLoans;
    private Double npaRate;
    private Double avgHealthScore;
    private BigDecimal totalExposure;
    private BigDecimal homeLoansExposure;
    private BigDecimal personalLoansExposure;
    private BigDecimal vehicleLoansExposure;
    private Long standardCount;
    private Long watchCount;
    private Long stressCount;
    private Long npaCount;
    private LocalDateTime createdAt;
}
