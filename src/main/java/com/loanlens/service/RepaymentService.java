package com.loanlens.service;

import com.loanlens.dto.request.RepaymentRequest;
import com.loanlens.dto.response.RepaymentResponse;
import com.loanlens.entity.LoanInstallment;
import com.loanlens.entity.Repayment;
import com.loanlens.enums.InstallmentStatus;
import com.loanlens.exception.BadRequestException;
import com.loanlens.exception.ResourceNotFoundException;
import com.loanlens.repository.LoanInstallmentRepository;
import com.loanlens.repository.RepaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepaymentService {

    private final RepaymentRepository repaymentRepository;
    private final LoanInstallmentRepository installmentRepository;

    @Transactional
    public RepaymentResponse recordRepayment(RepaymentRequest request) {
        LoanInstallment installment = installmentRepository.findById(request.getInstallmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found: " + request.getInstallmentId()));

        if (installment.getStatus() == InstallmentStatus.PAID) {
            throw new BadRequestException("Installment is already fully paid");
        }

        BigDecimal alreadyPaid = repaymentRepository.sumAmountPaidByInstallment(installment.getId());
        BigDecimal remaining = installment.getTotalAmount().subtract(alreadyPaid);

        if (request.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be positive");
        }

        int daysLate = 0;
        if (request.getPaymentDate().isAfter(installment.getDueDate())) {
            daysLate = (int) ChronoUnit.DAYS.between(installment.getDueDate(), request.getPaymentDate());
        }

        boolean isPartial = request.getAmountPaid().compareTo(remaining) < 0;

        Repayment repayment = Repayment.builder()
                .installment(installment)
                .amountPaid(request.getAmountPaid())
                .paymentDate(request.getPaymentDate())
                .daysLate(daysLate)
                .isPartial(isPartial)
                .build();

        repaymentRepository.save(repayment);

        // Update installment status
        BigDecimal newTotalPaid = alreadyPaid.add(request.getAmountPaid());
        if (newTotalPaid.compareTo(installment.getTotalAmount()) >= 0) {
            installment.setStatus(InstallmentStatus.PAID);
        } else if (newTotalPaid.compareTo(BigDecimal.ZERO) > 0) {
            installment.setStatus(InstallmentStatus.PARTIALLY_PAID);
        }
        installmentRepository.save(installment);

        return mapToResponse(repayment);
    }

    @Transactional(readOnly = true)
    public List<RepaymentResponse> getLoanRepaymentHistory(Long loanId) {
        return repaymentRepository.findRepaymentHistoryByLoan(loanId).stream()
                .map(this::mapToResponse).toList();
    }

    private RepaymentResponse mapToResponse(Repayment r) {
        return RepaymentResponse.builder()
                .id(r.getId())
                .installmentId(r.getInstallment().getId())
                .installmentNumber(r.getInstallment().getInstallmentNumber())
                .amountPaid(r.getAmountPaid())
                .paymentDate(r.getPaymentDate())
                .daysLate(r.getDaysLate())
                .isPartial(r.getIsPartial())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
