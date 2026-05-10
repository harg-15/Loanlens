package com.loanlens.entity;

import com.loanlens.enums.AlertStatus;
import com.loanlens.enums.RiskTier;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_tier")
    private RiskTier previousTier;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_tier", nullable = false)
    private RiskTier newTier;

    @Column(name = "alert_date", nullable = false)
    @Builder.Default
    private LocalDateTime alertDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AlertStatus status = AlertStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_officer_id")
    private User assignedOfficer;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
