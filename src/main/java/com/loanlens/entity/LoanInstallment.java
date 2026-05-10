package com.loanlens.entity;

import com.loanlens.enums.InstallmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_installments", indexes = {
    @Index(name = "idx_installment_due_date", columnList = "due_date"),
    @Index(name = "idx_installment_loan_id", columnList = "loan_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "principal_component", nullable = false, precision = 15, scale = 2)
    private BigDecimal principalComponent;

    @Column(name = "interest_component", nullable = false, precision = 15, scale = 2)
    private BigDecimal interestComponent;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InstallmentStatus status = InstallmentStatus.PENDING;

    @OneToMany(mappedBy = "installment", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Repayment> repayments = new ArrayList<>();
}
