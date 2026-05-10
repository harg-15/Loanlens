package com.loanlens.dto.response;

import com.loanlens.enums.LoanStatus;
import com.loanlens.enums.LoanType;
import com.loanlens.enums.RiskTier;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class LoanResponse {
    private Long id;
    private Long borrowerId;
    private String borrowerName;
    private LoanType loanType;
    private BigDecimal principal;
    private Double interestRate;
    private Integer tenureMonths;
    private LocalDate disbursementDate;
    private LoanStatus status;
    private RiskTier riskTier;
    private Integer healthScore;
    private String collateralDetails;
    private String assignedOfficerUsername;
    private BigDecimal emiAmount;
    private LocalDateTime createdAt;
}
