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
public class PartialPaymentStrategy implements ScoringStrategy {

    @Override
    public double computeScore(Loan loan, List<LoanInstallment> installments, List<Repayment> repayments) {
        LocalDate today = LocalDate.now();
        List<LoanInstallment> dueInstallments = installments.stream()
                .filter(i -> !i.getDueDate().isAfter(today))
                .toList();

        if (dueInstallments.isEmpty()) return 100.0;

        long partialCount = dueInstallments.stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PARTIALLY_PAID)
                .count();

        long paidCount = dueInstallments.stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PAID)
                .count();

        if (paidCount + partialCount == 0) return 50.0;

        double fullPaymentRatio = (double) paidCount / dueInstallments.size();
        return fullPaymentRatio * 100.0;
    }

    @Override
    public double getWeight() { return 20.0; }

    @Override
    public String getName() { return "PartialPayment"; }
}
