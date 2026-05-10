package com.loanlens.scheduler;

import com.loanlens.entity.Loan;
import com.loanlens.entity.LoanInstallment;
import com.loanlens.entity.Repayment;
import com.loanlens.entity.RiskAlert;
import com.loanlens.enums.InstallmentStatus;
import com.loanlens.enums.LoanStatus;
import com.loanlens.enums.RiskTier;
import com.loanlens.repository.*;
import com.loanlens.scoring.ScoringEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoringScheduler {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository installmentRepository;
    private final RepaymentRepository repaymentRepository;
    private final RiskAlertRepository riskAlertRepository;
    private final ScoringEngine scoringEngine;

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void runNightlyScoring() {
        log.info("Starting nightly scoring job");

        // Mark overdue installments
        List<LoanInstallment> overdueInstallments = installmentRepository.findOverdueInstallments(LocalDate.now());
        for (LoanInstallment inst : overdueInstallments) {
            if (inst.getStatus() == InstallmentStatus.PENDING) {
                inst.setStatus(InstallmentStatus.OVERDUE);
                installmentRepository.save(inst);
            }
        }

        // Score all active loans
        List<Loan> activeLoans = loanRepository.findAllActiveLoans();
        for (Loan loan : activeLoans) {
            try {
                List<LoanInstallment> installments = installmentRepository.findByLoanIdOrderByInstallmentNumber(loan.getId());
                List<Repayment> repayments = repaymentRepository.findRepaymentHistoryByLoan(loan.getId());

                int newScore = scoringEngine.computeHealthScore(loan, installments, repayments);
                RiskTier newTier = scoringEngine.classifyRiskTier(newScore);

                RiskTier previousTier = loan.getRiskTier();

                loan.setHealthScore(newScore);
                loan.setRiskTier(newTier);

                // Check if all installments paid — close loan
                boolean allPaid = installments.stream().allMatch(i -> i.getStatus() == InstallmentStatus.PAID);
                if (allPaid) {
                    loan.setStatus(LoanStatus.CLOSED);
                }

                loanRepository.save(loan);

                // Generate alert on tier degradation
                if (previousTier != newTier && isDegrade(previousTier, newTier)) {
                    RiskAlert alert = RiskAlert.builder()
                            .loan(loan)
                            .previousTier(previousTier)
                            .newTier(newTier)
                            .assignedOfficer(loan.getAssignedOfficer())
                            .build();
                    riskAlertRepository.save(alert);
                    log.info("Alert created for loan {}: {} -> {}", loan.getId(), previousTier, newTier);
                }

            } catch (Exception e) {
                log.error("Error scoring loan {}: {}", loan.getId(), e.getMessage());
            }
        }
        log.info("Nightly scoring job complete. Processed {} loans", activeLoans.size());
    }

    private boolean isDegrade(RiskTier prev, RiskTier next) {
        RiskTier[] tiers = {RiskTier.STANDARD, RiskTier.WATCH, RiskTier.STRESS, RiskTier.NPA};
        int prevIdx = -1, nextIdx = -1;
        for (int i = 0; i < tiers.length; i++) {
            if (tiers[i] == prev) prevIdx = i;
            if (tiers[i] == next) nextIdx = i;
        }
        return nextIdx > prevIdx;
    }
}

