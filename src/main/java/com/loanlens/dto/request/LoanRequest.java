package com.loanlens.dto.request;

import com.loanlens.enums.LoanType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanRequest {
    @NotNull
    private Long borrowerId;
    @NotNull
    private LoanType loanType;
    @NotNull @DecimalMin("1000.0")
    private BigDecimal principal;
    @NotNull @DecimalMin("0.01") @DecimalMax("50.0")
    private Double interestRate;
    @NotNull @Min(1) @Max(360)
    private Integer tenureMonths;
    @NotNull
    private LocalDate disbursementDate;
    private String collateralDetails;
    private Long assignedOfficerId;
}
