package com.loanlens.dto.response;

import com.loanlens.enums.AlertStatus;
import com.loanlens.enums.RiskTier;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RiskAlertResponse {
    private Long id;
    private Long loanId;
    private String borrowerName;
    private RiskTier previousTier;
    private RiskTier newTier;
    private LocalDateTime alertDate;
    private AlertStatus status;
    private String assignedOfficerUsername;
    private String notes;
}
