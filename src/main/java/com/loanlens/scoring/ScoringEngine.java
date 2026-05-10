package com.loanlens.scoring;

import com.loanlens.entity.Loan;
import com.loanlens.entity.LoanInstallment;
import com.loanlens.entity.Repayment;
import com.loanlens.enums.RiskTier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoringEngine {

    private final List<ScoringStrategy> strategies;

    public int computeHealthScore(Loan loan, List<LoanInstallment> installments, List<Repayment> repayments) {
        if (installments.isEmpty()) return 100;

        double totalWeight = strategies.stream().mapToDouble(ScoringStrategy::getWeight).sum();
        double weightedScore = strategies.stream()
                .mapToDouble(s -> s.computeScore(loan, installments, repayments) * s.getWeight())
                .sum();

        int score = (int) Math.round(weightedScore / totalWeight);
        return Math.max(0, Math.min(100, score));
    }

    public RiskTier classifyRiskTier(int healthScore) {
        if (healthScore >= 80) return RiskTier.STANDARD;
        if (healthScore >= 60) return RiskTier.WATCH;
        if (healthScore >= 40) return RiskTier.STRESS;
        return RiskTier.NPA;
    }
}
