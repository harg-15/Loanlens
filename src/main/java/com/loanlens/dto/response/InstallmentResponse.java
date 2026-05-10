package com.loanlens.dto.response;

import com.loanlens.enums.InstallmentStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class InstallmentResponse {
    private Long id;
    private Long loanId;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal principalComponent;
    private BigDecimal interestComponent;
    private BigDecimal totalAmount;
    private InstallmentStatus status;
    private BigDecimal totalPaid;
}
