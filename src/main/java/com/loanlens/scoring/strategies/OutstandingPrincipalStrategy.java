package com.loanlens.scoring.strategies;

import com.loanlens.entity.Loan;
import com.loanlens.entity.LoanInstallment;
import com.loanlens.entity.Repayment;
import com.loanlens.enums.InstallmentStatus;
import com.loanlens.scoring.ScoringStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class OutstandingPrincipalStrategy implements ScoringStrategy {

    @Override
    public double computeScore(Loan loan, List<LoanInstallment> installments, List<Repayment> repayments) {
        LocalDate today = LocalDate.now();

        // Expected principal collected by now
        BigDecimal expectedPrincipal = installments.stream()
                .filter(i -> !i.getDueDate().isAfter(today))
                .map(LoanInstallment::getPrincipalComponent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Actual principal collected (paid installments)
        BigDecimal actualPrincipal = installments.stream()
                .filter(i -> !i.getDueDate().isAfter(today) && i.getStatus() == InstallmentStatus.PAID)
                .map(LoanInstallment::getPrincipalComponent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (expectedPrincipal.compareTo(BigDecimal.ZERO) == 0) return 100.0;

        double ratio = actualPrincipal.doubleValue() / expectedPrincipal.doubleValue();
        return ratio * 100.0;
    }

    @Override
    public double getWeight() { return 15.0; }

    @Override
    public String getName() { return "OutstandingPrincipal"; }
}
