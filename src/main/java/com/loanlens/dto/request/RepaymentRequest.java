package com.loanlens.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RepaymentRequest {
    @NotNull
    private Long installmentId;
    @NotNull @DecimalMin("1.0")
    private BigDecimal amountPaid;
    @NotNull
    private LocalDate paymentDate;
}
