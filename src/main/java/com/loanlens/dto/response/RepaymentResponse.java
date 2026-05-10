package com.loanlens.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RepaymentResponse {
    private Long id;
    private Long installmentId;
    private Integer installmentNumber;
    private BigDecimal amountPaid;
    private LocalDate paymentDate;
    private Integer daysLate;
    private Boolean isPartial;
    private LocalDateTime createdAt;
}
