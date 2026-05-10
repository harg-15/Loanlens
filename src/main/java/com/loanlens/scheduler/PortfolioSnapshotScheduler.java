package com.loanlens.scheduler;

import com.loanlens.entity.PortfolioSnapshot;
import com.loanlens.enums.LoanType;
import com.loanlens.enums.RiskTier;
import com.loanlens.repository.LoanRepository;
import com.loanlens.repository.PortfolioSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortfolioSnapshotScheduler {

    private final LoanRepository loanRepository;
    private final PortfolioSnapshotRepository snapshotRepository;

    @Scheduled(cron = "0 30 1 * * *")
    @Transactional
    public void takePortfolioSnapshot() {
        LocalDate today = LocalDate.now();
        if (snapshotRepository.existsBySnapshotDate(today)) {
            log.info("Portfolio snapshot for {} already exists, skipping", today);
            return;
        }

        log.info("Taking portfolio snapshot for {}", today);

        long totalActive = loanRepository.countActiveLoans();
        long standardCount = loanRepository.countByRiskTierAndStatusActive(RiskTier.STANDARD);
        long watchCount    = loanRepository.countByRiskTierAndStatusActive(RiskTier.WATCH);
        long stressCount   = loanRepository.countByRiskTierAndStatusActive(RiskTier.STRESS);
        long npaCount      = loanRepository.countByRiskTierAndStatusActive(RiskTier.NPA);

        double npaRate = totalActive > 0 ? (double) npaCount / totalActive * 100.0 : 0.0;
        Double avgScore = loanRepository.avgHealthScore();

        BigDecimal homeExp     = loanRepository.sumPrincipalByLoanType(LoanType.HOME_LOAN);
        BigDecimal personalExp = loanRepository.sumPrincipalByLoanType(LoanType.PERSONAL_LOAN);
        BigDecimal vehicleExp  = loanRepository.sumPrincipalByLoanType(LoanType.VEHICLE_LOAN);
        BigDecimal totalExp    = loanRepository.sumTotalActivePrincipal();

        PortfolioSnapshot snapshot = PortfolioSnapshot.builder()
                .snapshotDate(today)
                .totalActiveLoans(totalActive)
                .npaLoans(npaCount)
                .npaRate(npaRate)
                .avgHealthScore(avgScore != null ? avgScore : 0.0)
                .totalExposure(totalExp)
                .homeLoansExposure(homeExp)
                .personalLoansExposure(personalExp)
                .vehicleLoansExposure(vehicleExp)
                .standardCount(standardCount)
                .watchCount(watchCount)
                .stressCount(stressCount)
                .npaCount(npaCount)
                .build();

        snapshotRepository.save(snapshot);
        log.info("Portfolio snapshot saved: total={}, NPA rate={}%", totalActive, String.format("%.2f", npaRate));
    }
}
