package com.loanlens.repository;

import com.loanlens.entity.LoanInstallment;
import com.loanlens.enums.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    List<LoanInstallment> findByLoanIdOrderByInstallmentNumber(Long loanId);

    @Query("SELECT li FROM LoanInstallment li WHERE li.loan.id = :loanId AND li.status IN ('PENDING', 'OVERDUE', 'PARTIALLY_PAID') ORDER BY li.installmentNumber")
    List<LoanInstallment> findPendingInstallments(@Param("loanId") Long loanId);

    @Query("SELECT li FROM LoanInstallment li WHERE li.dueDate < :today AND li.status IN ('PENDING', 'PARTIALLY_PAID')")
    List<LoanInstallment> findOverdueInstallments(@Param("today") LocalDate today);

    @Query("SELECT li FROM LoanInstallment li WHERE li.loan.id = :loanId AND li.status = 'OVERDUE'")
    List<LoanInstallment> findOverdueByLoan(@Param("loanId") Long loanId);

    @Query("SELECT li FROM LoanInstallment li WHERE li.loan.id = :loanId AND li.status = :status")
    List<LoanInstallment> findByLoanIdAndStatus(@Param("loanId") Long loanId, @Param("status") InstallmentStatus status);

    @Query("SELECT COUNT(li) FROM LoanInstallment li WHERE li.loan.id = :loanId AND li.dueDate <= :asOf")
    Long countDueByLoan(@Param("loanId") Long loanId, @Param("asOf") LocalDate asOf);
}
