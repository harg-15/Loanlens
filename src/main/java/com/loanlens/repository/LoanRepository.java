package com.loanlens.repository;

import com.loanlens.entity.Loan;
import com.loanlens.enums.LoanStatus;
import com.loanlens.enums.LoanType;
import com.loanlens.enums.RiskTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    List<Loan> findByBorrowerId(Long borrowerId);

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByAssignedOfficerId(Long officerId);

    List<Loan> findByRiskTier(RiskTier riskTier);

    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE'")
    List<Loan> findAllActiveLoans();

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'ACTIVE'")
    long countActiveLoans();

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.riskTier = :tier AND l.status = 'ACTIVE'")
    Long countByRiskTierAndStatusActive(@Param("tier") RiskTier tier);

    @Query("SELECT COALESCE(SUM(l.principal), 0) FROM Loan l WHERE l.loanType = :type AND l.status = 'ACTIVE'")
    BigDecimal sumPrincipalByLoanType(@Param("type") LoanType type);

    @Query("SELECT COALESCE(SUM(l.principal), 0) FROM Loan l WHERE l.status = 'ACTIVE'")
    BigDecimal sumTotalActivePrincipal();

    @Query("SELECT COALESCE(AVG(l.healthScore), 0) FROM Loan l WHERE l.status = 'ACTIVE'")
    Double avgHealthScore();
}
