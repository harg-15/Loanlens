package com.loanlens.dto.request;

import com.loanlens.enums.LoanStatus;
import lombok.Data;

@Data
public class LoanUpdateRequest {
    private LoanStatus status;
    private String collateralDetails;
    private Long assignedOfficerId;
}
