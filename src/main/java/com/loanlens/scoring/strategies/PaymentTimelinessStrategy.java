package com.loanlens.scoring.strategies;

import com.loanlens.entity.Loan;
import com.loanlens.entity.LoanInstallment;
import com.loanlens.entity.Repayment;
import com.loanlens.enums.InstallmentStatus;
import com.loanlens.scoring.ScoringStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class PaymentTimelinessStrategy implements ScoringStrategy {

    @Override
    public double computeScore(Loan loan, List<LoanInstallment> installments, List<Repayment> repayments) {
        LocalDate today = LocalDate.now();
        List<LoanInstallment> dueInstallments = installments.stream()
                .filter(i -> !i.getDueDate().isAfter(today))
                .toList();

        if (dueInstallments.isEmpty()) return 100.0;

        long paidOnTime = dueInstallments.stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PAID
                        && i.getRepayments() != null
                        && !i.getRepayments().isEmpty()
                        && i.getRepayments().stream().allMatch(r -> r.getDaysLate() == 0))
                .count();

        return (double) paidOnTime / dueInstallments.size() * 100.0;
    }

    @Override
    public double getWeight() { return 35.0; }

    @Override
    public String getName() { return "PaymentTimeliness"; }
}
