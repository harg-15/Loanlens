package com.loanlens;

import com.loanlens.scoring.ScoringEngine;
import com.loanlens.entity.Loan;
import com.loanlens.entity.LoanInstallment;
import com.loanlens.entity.Repayment;
import com.loanlens.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LoanLensApplicationTests {

    @Autowired
    private ScoringEngine scoringEngine;

    @Test
    void contextLoads() {
    }

    @Test
    void scoringEngineClassifiesRiskTiersCorrectly() {
        assertEquals(RiskTier.STANDARD, scoringEngine.classifyRiskTier(100));
        assertEquals(RiskTier.STANDARD, scoringEngine.classifyRiskTier(80));
        assertEquals(RiskTier.WATCH,    scoringEngine.classifyRiskTier(79));
        assertEquals(RiskTier.WATCH,    scoringEngine.classifyRiskTier(60));
        assertEquals(RiskTier.STRESS,   scoringEngine.classifyRiskTier(59));
        assertEquals(RiskTier.STRESS,   scoringEngine.classifyRiskTier(40));
        assertEquals(RiskTier.NPA,      scoringEngine.classifyRiskTier(39));
        assertEquals(RiskTier.NPA,      scoringEngine.classifyRiskTier(0));
    }

    @Test
    void scoringEngineReturns100ForLoanWithNoInstallments() {
        Loan loan = Loan.builder()
                .principal(BigDecimal.valueOf(100000))
                .interestRate(10.0)
                .tenureMonths(12)
                .disbursementDate(LocalDate.now().minusMonths(1))
                .status(LoanStatus.ACTIVE)
                .riskTier(RiskTier.STANDARD)
                .build();

        int score = scoringEngine.computeHealthScore(loan, List.of(), List.of());
        assertEquals(100, score);
    }

    @Test
    void scoringEngineScoresCappedBetween0And100() {
        Loan loan = Loan.builder()
                .principal(BigDecimal.valueOf(500000))
                .interestRate(8.5)
                .tenureMonths(24)
                .disbursementDate(LocalDate.now().minusMonths(6))
                .status(LoanStatus.ACTIVE)
                .riskTier(RiskTier.STANDARD)
                .build();

        LoanInstallment overdue = LoanInstallment.builder()
                .installmentNumber(1)
                .dueDate(LocalDate.now().minusMonths(5))
                .principalComponent(BigDecimal.valueOf(19000))
                .interestComponent(BigDecimal.valueOf(3541))
                .totalAmount(BigDecimal.valueOf(22541))
                .status(InstallmentStatus.OVERDUE)
                .build();
        overdue.setLoan(loan);

        int score = scoringEngine.computeHealthScore(loan, List.of(overdue), List.of());
        assertTrue(score >= 0 && score <= 100, "Score must be 0-100 but was " + score);
    }
}
