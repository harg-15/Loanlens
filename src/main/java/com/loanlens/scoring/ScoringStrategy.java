package com.loanlens.scoring;

import com.loanlens.entity.Loan;
import com.loanlens.entity.LoanInstallment;
import com.loanlens.entity.Repayment;
import java.util.List;

public interface ScoringStrategy {
    double computeScore(Loan loan, List<LoanInstallment> installments, List<Repayment> repayments);
    double getWeight();
    String getName();
}
