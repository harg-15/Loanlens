package com.loanlens.scoring.strategies;

import com.loanlens.entity.Loan;
import com.loanlens.entity.LoanInstallment;
import com.loanlens.entity.Repayment;
import com.loanlens.enums.InstallmentStatus;
import com.loanlens.scoring.ScoringStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
public class OverdueStreakStrategy implements ScoringStrategy {

    @Override
    public double computeScore(Loan loan, List<LoanInstallment> installments, List<Repayment> repayments) {
        LocalDate today = LocalDate.now();

        // Find consecutive overdue installments from the most recent
        List<LoanInstallment> sorted = installments.stream()
                .filter(i -> !i.getDueDate().isAfter(today))
                .sorted(Comparator.comparing(LoanInstallment::getInstallmentNumber).reversed())
                .toList();

        int streak = 0;
        for (LoanInstallment inst : sorted) {
            if (inst.getStatus() == InstallmentStatus.OVERDUE ||
                inst.getStatus() == InstallmentStatus.PARTIALLY_PAID) {
                streak++;
            } else {
                break;
            }
        }

        if (streak == 0) return 100.0;
        if (streak == 1) return 70.0;
        if (streak == 2) return 40.0;
        if (streak == 3) return 15.0;
        return 0.0;
    }

    @Override
    public double getWeight() { return 30.0; }

    @Override
    public String getName() { return "OverdueStreak"; }
}
