package com.loanlens.repository;

import com.loanlens.entity.RiskAlert;
import com.loanlens.enums.AlertStatus;
import com.loanlens.enums.RiskTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RiskAlertRepository extends JpaRepository<RiskAlert, Long> {

    List<RiskAlert> findByAssignedOfficerId(Long officerId);

    List<RiskAlert> findByLoanId(Long loanId);

    List<RiskAlert> findByStatus(AlertStatus status);

    @Query("SELECT ra FROM RiskAlert ra WHERE ra.assignedOfficer.id = :officerId AND ra.status = 'OPEN'")
    List<RiskAlert> findOpenAlertsByOfficer(@Param("officerId") Long officerId);

    @Query("SELECT ra FROM RiskAlert ra WHERE ra.loan.id = :loanId ORDER BY ra.alertDate DESC")
    List<RiskAlert> findLatestAlertsByLoan(@Param("loanId") Long loanId);

    boolean existsByLoanIdAndNewTier(Long loanId, RiskTier newTier);
}
