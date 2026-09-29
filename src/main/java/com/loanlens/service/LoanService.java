package com.loanlens.service;

import com.loanlens.dto.request.LoanRequest;
import com.loanlens.dto.request.LoanUpdateRequest;
import com.loanlens.dto.response.InstallmentResponse;
import com.loanlens.dto.response.LoanResponse;
import com.loanlens.entity.*;
import com.loanlens.enums.LoanStatus;
import com.loanlens.enums.LoanType;
import com.loanlens.enums.RiskTier;
import com.loanlens.exception.BadRequestException;
import com.loanlens.exception.ResourceNotFoundException;
import com.loanlens.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BorrowerRepository borrowerRepository;
    private final UserRepository userRepository;
    private final LoanInstallmentRepository installmentRepository;
    private final RepaymentRepository repaymentRepository;

    @Transactional
    public LoanResponse createLoan(LoanRequest request) {
        Borrower borrower = borrowerRepository.findById(request.getBorrowerId())
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found: " + request.getBorrowerId()));

        User officer = null;
        if (request.getAssignedOfficerId() != null) {
            officer = userRepository.findById(request.getAssignedOfficerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Officer not found: " + request.getAssignedOfficerId()));
        }

        Loan loan = Loan.builder()
                .borrower(borrower)
                .loanType(request.getLoanType())
                .principal(request.getPrincipal())
                .interestRate(request.getInterestRate())
                .tenureMonths(request.getTenureMonths())
                .disbursementDate(request.getDisbursementDate())
                .collateralDetails(request.getCollateralDetails())
                .assignedOfficer(officer)
                .build();

        loanRepository.save(loan);
        generateInstallmentSchedule(loan);

        return mapToResponse(loan);
    }

    private void generateInstallmentSchedule(Loan loan) {
        BigDecimal principal = loan.getPrincipal();
        double annualRate = loan.getInterestRate();
        int months = loan.getTenureMonths();

        double monthlyRate = annualRate / 12.0 / 100.0;

        // EMI = P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal emi;
        if (monthlyRate == 0) {
            emi = principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        } else {
            double factor = Math.pow(1 + monthlyRate, months);
            double emiDouble = principal.doubleValue() * monthlyRate * factor / (factor - 1);
            emi = BigDecimal.valueOf(emiDouble).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal outstandingPrincipal = principal;
        LocalDate dueDate = loan.getDisbursementDate().plusMonths(1);

        List<LoanInstallment> installments = new ArrayList<>();
        for (int i = 1; i <= months; i++) {
            BigDecimal interestComponent = outstandingPrincipal
                    .multiply(BigDecimal.valueOf(monthlyRate))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal principalComponent;
            if (i == months) {
                // Last installment: pay remaining principal
                principalComponent = outstandingPrincipal;
            } else {
                principalComponent = emi.subtract(interestComponent);
            }

            BigDecimal totalAmount = principalComponent.add(interestComponent);

            LoanInstallment installment = LoanInstallment.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .dueDate(dueDate)
                    .principalComponent(principalComponent)
                    .interestComponent(interestComponent)
                    .totalAmount(totalAmount)
                    .build();

            installments.add(installment);
            outstandingPrincipal = outstandingPrincipal.subtract(principalComponent);
            dueDate = dueDate.plusMonths(1);
        }

        installmentRepository.saveAll(installments);
        loan.setInstallments(installments);
    }

    @Transactional(readOnly = true)
    public LoanResponse getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + id));
        return mapToResponse(loan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByBorrower(Long borrowerId) {
        if (!borrowerRepository.existsById(borrowerId)) {
            throw new ResourceNotFoundException("Borrower not found: " + borrowerId);
        }
        return loanRepository.findByBorrowerId(borrowerId).stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansWithFilters(LoanType type, LoanStatus status, RiskTier riskTier, Long officerId) {
        Specification<Loan> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (type != null) predicates.add(cb.equal(root.get("loanType"), type));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (riskTier != null) predicates.add(cb.equal(root.get("riskTier"), riskTier));
            if (officerId != null) predicates.add(cb.equal(root.get("assignedOfficer").get("id"), officerId));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return loanRepository.findAll(spec).stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public LoanResponse updateLoan(Long id, LoanUpdateRequest request) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + id));

        if (request.getStatus() != null) {
            loan.setStatus(request.getStatus());
        }
        if (request.getCollateralDetails() != null) {
            loan.setCollateralDetails(request.getCollateralDetails());
        }
        if (request.getAssignedOfficerId() != null) {
            User officer = userRepository.findById(request.getAssignedOfficerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Officer not found: " + request.getAssignedOfficerId()));
            loan.setAssignedOfficer(officer);
        }

        loanRepository.save(loan);
        return mapToResponse(loan);
    }

    @Transactional
    public void deleteLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + id));

        boolean hasRepayments = loan.getInstallments().stream()
                .anyMatch(installment -> !installment.getRepayments().isEmpty());
        if (hasRepayments) {
            throw new BadRequestException("Cannot delete a loan that already has recorded repayments: " + id);
        }

        loanRepository.delete(loan);
    }

    @Transactional(readOnly = true)
    public List<InstallmentResponse> getLoanInstallments(Long loanId) {
        if (!loanRepository.existsById(loanId)) {
            throw new ResourceNotFoundException("Loan not found: " + loanId);
        }
        return installmentRepository.findByLoanIdOrderByInstallmentNumber(loanId).stream()
                .map(inst -> {
                    BigDecimal totalPaid = repaymentRepository.sumAmountPaidByInstallment(inst.getId());
                    return InstallmentResponse.builder()
                            .id(inst.getId())
                            .loanId(loanId)
                            .installmentNumber(inst.getInstallmentNumber())
                            .dueDate(inst.getDueDate())
                            .principalComponent(inst.getPrincipalComponent())
                            .interestComponent(inst.getInterestComponent())
                            .totalAmount(inst.getTotalAmount())
                            .status(inst.getStatus())
                            .totalPaid(totalPaid)
                            .build();
                }).toList();
    }

    private LoanResponse mapToResponse(Loan loan) {
        BigDecimal emi = BigDecimal.ZERO;
        if (loan.getInstallments() != null && !loan.getInstallments().isEmpty()) {
            emi = loan.getInstallments().get(0).getTotalAmount();
        }
        return LoanResponse.builder()
                .id(loan.getId())
                .borrowerId(loan.getBorrower().getId())
                .borrowerName(loan.getBorrower().getFullName())
                .loanType(loan.getLoanType())
                .principal(loan.getPrincipal())
                .interestRate(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .disbursementDate(loan.getDisbursementDate())
                .status(loan.getStatus())
                .riskTier(loan.getRiskTier())
                .healthScore(loan.getHealthScore())
                .collateralDetails(loan.getCollateralDetails())
                .assignedOfficerUsername(loan.getAssignedOfficer() != null ? loan.getAssignedOfficer().getUsername() : null)
                .emiAmount(emi)
                .createdAt(loan.getCreatedAt())
                .build();
    }
}

