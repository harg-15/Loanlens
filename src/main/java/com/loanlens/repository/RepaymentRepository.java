package com.loanlens.repository;

import com.loanlens.entity.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

    List<Repayment> findByInstallmentId(Long installmentId);

    @Query("SELECT r FROM Repayment r WHERE r.installment.loan.id = :loanId ORDER BY r.paymentDate DESC")
    List<Repayment> findRepaymentHistoryByLoan(@Param("loanId") Long loanId);

    @Query("SELECT COALESCE(SUM(r.amountPaid), 0) FROM Repayment r WHERE r.installment.id = :installmentId")
    java.math.BigDecimal sumAmountPaidByInstallment(@Param("installmentId") Long installmentId);
}
