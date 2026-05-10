package com.loanlens.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_snapshots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "snapshot_date", unique = true, nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "total_active_loans")
    private Long totalActiveLoans;

    @Column(name = "npa_loans")
    private Long npaLoans;

    @Column(name = "npa_rate")
    private Double npaRate;

    @Column(name = "avg_health_score")
    private Double avgHealthScore;

    @Column(name = "total_exposure", precision = 20, scale = 2)
    private BigDecimal totalExposure;

    @Column(name = "home_loans_exposure", precision = 20, scale = 2)
    private BigDecimal homeLoansExposure;

    @Column(name = "personal_loans_exposure", precision = 20, scale = 2)
    private BigDecimal personalLoansExposure;

    @Column(name = "vehicle_loans_exposure", precision = 20, scale = 2)
    private BigDecimal vehicleLoansExposure;

    @Column(name = "standard_count")
    private Long standardCount;

    @Column(name = "watch_count")
    private Long watchCount;

    @Column(name = "stress_count")
    private Long stressCount;

    @Column(name = "npa_count")
    private Long npaCount;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
